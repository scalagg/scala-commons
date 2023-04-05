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

    var servers = CopyOnWriteArrayList<AbstractServer>()

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
            ::run, 0L,
            500L, TimeUnit.MILLISECONDS
        )
    }

    override fun run()
    {
        val things = this.container.sync()
            .hgetall("${ServerSyncPipeline.NAMESPACE}:serversv2")
            .mapValues {
                Serializers.gson.fromJson(it.value, AbstractServer::class.java)
            }
            .toMutableMap()

        val expirations = this.container.sync()
            .hgetall(
                "${ServerSyncPipeline.NAMESPACE}:heartbeats"
            )

        for ((k, v) in expirations)
        {
            if (System.currentTimeMillis() >= v.toLong())
            {
                things.remove(k)
            }
        }

        this.servers = CopyOnWriteArrayList(things.values)
    }
}
