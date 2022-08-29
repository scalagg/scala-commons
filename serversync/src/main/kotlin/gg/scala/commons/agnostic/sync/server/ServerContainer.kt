package gg.scala.commons.agnostic.sync.server

import gg.scala.commons.agnostic.sync.ScalaServerSync
import gg.scala.commons.agnostic.sync.ServerSyncPipeline
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
        ScalaServerSync.scheduleAtFixedRate(
            this, 0L, 500L, TimeUnit.MILLISECONDS
        )
    }

    override fun run()
    {
        this.servers.clear()
        this.servers.addAll(
            this.container.sync()
                .hgetall(ServerSyncPipeline.NAMESPACE)
                .map {
                    Serializers.gson.fromJson(it.value, AbstractServer::class.java)
                }
        )
    }
}
