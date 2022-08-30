package gg.scala.commons.agnostic.sync.server

import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.agnostic.sync.ServerSyncPipeline
import io.lettuce.core.cluster.PipelinedRedisFuture
import io.lettuce.core.pubsub.RedisPubSubListener
import net.evilblock.cubed.serializers.Serializers
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

    fun allServers() = this.servers

    inline fun <reified T> allServers() =
        this.servers.filterIsInstance<T>()

    fun getServer(id: String) = this.servers
        .filter {
            it.id.equals(id, true)
        }

    inline fun <reified T> getServer(id: String) =
        this.getServer(id) as T

    fun getServersInGroup(group: String) = this.servers
        .filter {
            group in it.groups
        }

    inline fun <reified T> getServersInGroup(group: String) =
        this.getServersInGroup(group) as T

    fun configure()
    {
        ServerSync.scheduleAtFixedRate(
            this, 0L, 500L, TimeUnit.MILLISECONDS
        )
    }

    override fun run()
    {
        this.servers.clear()
        this.servers.addAll(
            this.container.sync()
                .keys("${ServerSyncPipeline.NAMESPACE}:servers:*")
                .mapNotNull {
                    this.container.sync().get(it)
                }
                .map {
                    Serializers.gson.fromJson(it, AbstractServer::class.java)
                }
        )
    }
}
