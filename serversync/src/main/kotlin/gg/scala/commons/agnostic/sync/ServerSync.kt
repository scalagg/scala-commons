package gg.scala.commons.agnostic.sync

import gg.scala.commons.agnostic.sync.event.EventBusPipeline
import gg.scala.commons.agnostic.sync.server.AbstractServer
import gg.scala.commons.agnostic.sync.server.SelfServerService
import gg.scala.commons.agnostic.sync.server.ServerContainer
import gg.scala.commons.agnostic.sync.server.impl.GameServer
import gg.scala.commons.agnostic.sync.server.impl.ProxyServer
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializer
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * @author GrowlyX
 * @since 8/29/2022
 */
object ServerSync : ScheduledExecutorService by Executors
    .newSingleThreadScheduledExecutor()
{
    lateinit var local: AbstractServer

    // default casts
    fun getLocalGameServer() = this.local as GameServer
    fun getLocalProxyServer() = this.local as ProxyServer

    fun configureIndependent()
    {
        Serializers.useGsonBuilderThenRebuild {
            it.registerTypeAdapter(
                AbstractServer::class.java,
                AbstractTypeSerializer<AbstractServer>()
            )
        }

        ServerSyncPipeline.usePipeline {
            this.listen(EventBusPipeline)
            this.connect().toCompletableFuture().join()
        }

        ServerContainer.configure()
    }

    inline fun <reified T : AbstractServer> configure(
        server: T, noinline update: T.() -> Unit
    ): SelfServerService<T>
    {
        this.configureIndependent()
        this.local = server

        return SelfServerService(server, update) {
            """
                {
                   "type": "${T::class.java.name}",
                   "properties": ${Serializers.gson.toJson(it)}
                }
            """.trimIndent()
        }.apply {
            configure()
        }
    }
}
