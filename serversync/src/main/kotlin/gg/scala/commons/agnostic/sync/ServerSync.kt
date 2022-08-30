package gg.scala.commons.agnostic.sync

import gg.scala.commons.agnostic.sync.event.EventBusPipeline
import gg.scala.commons.agnostic.sync.server.AbstractServer
import gg.scala.commons.agnostic.sync.server.SelfServerService
import gg.scala.commons.agnostic.sync.server.ServerContainer
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializer
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * @author GrowlyX
 * @since 8/29/2022
 */
object ServerSync : ScheduledExecutorService by Executors
    .newSingleThreadScheduledExecutor()
{
    @Suppress("UNCHECKED_CAST")
    fun <T : AbstractServer> configure(
        server: T,
        update: T.() -> Unit
    ): CompletionStage<Void>
    {
        Serializers.create {
            this.registerTypeAdapter(
                AbstractServer::class.java,
                AbstractTypeSerializer<AbstractServer>()
            )
        }

        ServerSyncPipeline.usePipeline {
            this.listen(EventBusPipeline)
            this.connect().toCompletableFuture().join()
        }

        ServerContainer.configure()

        return SelfServerService(server) {
            update.invoke(it as T)
        }.configure()
    }
}
