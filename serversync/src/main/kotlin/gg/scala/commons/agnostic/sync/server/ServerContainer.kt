package gg.scala.commons.agnostic.sync.server

import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.agnostic.sync.ServerSyncPipeline
import net.evilblock.cubed.serializers.Serializers
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

    private var servers = mutableMapOf<String, AbstractServer>()
    private var serverLockObject = Any()

    fun allServers() = this.servers.toMap()

    inline fun <reified T> allServers() =
        allServers().values.filterIsInstance<T>()

    fun getServer(id: String) = allServers().values
        .find {
            it.id.equals(id, true)
        }

    inline fun <reified T> getServer(id: String) =
        this.getServer(id) as T

    fun getServersInGroup(group: String) = allServers().values
        .filter {
            group in it.groups
        }

    inline fun <reified T> getServersInGroupCasted(group: String) =
        this.getServersInGroup(group).filterIsInstance<T>()

    fun configure()
    {
        ServerSync.scheduleAtFixedRate(
            ::safeRun, 0L,
            500L, TimeUnit.MILLISECONDS
        )
    }

    private fun safeRun() = runCatching(::run).onFailure { it.printStackTrace() }

    override fun run()
    {
        val rawServerList = this.container.sync()
            .hgetall("${ServerSyncPipeline.NAMESPACE}:serversv2")

        val mappedServers = rawServerList
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
                mappedServers.remove(k)
            }
        }

        mappedServers.forEach {
            if (servers[it.key] == null)
            {
                servers[it.key] = it.value
            }
        }

        servers.toMap().forEach { (t, _) ->
            if (mappedServers[t] == null)
            {
                servers.remove(t)
            }
        }
    }
}
