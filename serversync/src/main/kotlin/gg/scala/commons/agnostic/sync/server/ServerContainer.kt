package gg.scala.commons.agnostic.sync.server

import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.agnostic.sync.ServerSyncPipeline
import gg.scala.commons.agnostic.sync.event.EventBus
import gg.scala.commons.agnostic.sync.event.events.ServerDisconnectEvent
import net.evilblock.cubed.serializers.Serializers
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 8/29/2022
 */
object ServerContainer : Runnable
{
    val container = ServerSyncPipeline
        .usePipeline {
            this.internal().connect()
        }!!

    val servers = CopyOnWriteArrayList<AbstractServer>()

    private var previousServerList = setOf<Pair<String, String>>()
    private var accountedFor = ConcurrentHashMap.newKeySet<String>()

    fun allServers() = this.servers

    inline fun <reified T> allServers() =
        this.servers.filterIsInstance<T>()

    fun getServer(id: String) = this.servers
        .find {
            it.id.equals(id, true)
        }

    inline fun <reified T> getServer(id: String) =
        this.getServer(id) as T

    fun getServersInGroup(group: String) = this.servers
        .filter {
            group in it.groups
        }

    inline fun <reified T> getServersInGroupCasted(group: String) =
        this.getServersInGroup(group).filterIsInstance<T>()

    fun configure()
    {
        ServerSync.scheduleAtFixedRate(
            {
                kotlin.runCatching {
                    this.run()
                }.onFailure {
                    it.printStackTrace()
                }
            },
            0L, 500L, TimeUnit.MILLISECONDS
        )

        EventBus.subscribe<ServerDisconnectEvent>()
            .listen {
                if (it.reason != "timed out")
                {
                    this.accountedFor.add(it.id)
                }
            }
    }

    override fun run()
    {
        val newServerList = this.container.sync()
            .keys("${ServerSyncPipeline.NAMESPACE}:servers:*")
            .mapNotNull {
                this.container.sync().get(it)
            }
            .map {
                Serializers.gson.fromJson(it, AbstractServer::class.java)
            }

        this.previousServerList = this.servers
            .map {
                it.id to it::class.simpleName!!
            }
            .toSet()

        val newServerListIds = newServerList.map { it.id }

        val removedServers = this.previousServerList
            .filter {
                it.first !in newServerListIds
            }

        for (removedServer in removedServers)
        {
            // Assuming this server has received some sort of disconnection
            // heartbeat and that it has been removed MANUALLY, not expired.
            if (removedServer.first in this.accountedFor)
            {
                this.accountedFor.remove(removedServer.first)
                continue
            }

            // Assume that this server has timed out, and its latest
            // heartbeat (<5s) has expired.

            // We'll publish a local disconnect event for our event handlers
            // to handle the logic for (as this process is run on each individual
            // server being listening on these channels).
            val disconnection = ServerDisconnectEvent(
                removedServer.first, removedServer.second, "timed out"
            )

            disconnection.push(local = true)
        }

        this.servers.clear()
        this.servers.addAll(newServerList)
    }
}
