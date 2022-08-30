package gg.scala.commons.agnostic.sync.server

import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.agnostic.sync.ServerSyncPipeline
import gg.scala.commons.agnostic.sync.event.events.ServerConnectedEvent
import gg.scala.commons.agnostic.sync.event.events.ServerDisconnectEvent
import net.evilblock.cubed.serializers.Serializers
import java.util.concurrent.CompletionStage
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 8/29/2022
 */
class SelfServerService(
    private val server: AbstractServer,
    private val updateMeta: (AbstractServer) -> Unit
)
{
    fun configure(): CompletionStage<Void>
    {
        this.updateMeta.invoke(this.server)

        return ServerContainer.container.async()
            .hset(
                ServerSyncPipeline.NAMESPACE, this.server.id,
                Serializers.gson.toJson(this.server)
            )
            .thenRun {
                val event = ServerConnectedEvent(
                    this.server.id, this.server.javaClass.typeName
                )

                event.push()

                ServerSync.scheduleAtFixedRate({
                    this.server.lastHeartbeat = System.currentTimeMillis()
                    this.updateMeta.invoke(this.server)

                    ServerContainer.container.sync()
                        .hset(
                            ServerSyncPipeline.NAMESPACE, this.server.id,
                            Serializers.gson.toJson(this.server)
                        )
                }, 0L, 500L, TimeUnit.MILLISECONDS)
            }
    }

    fun disconnect(reason: String = "unknown"): CompletionStage<Void>
    {
        return ServerContainer.container.async()
            .hdel(
                ServerSyncPipeline.NAMESPACE, this.server.id
            )
            .thenRun {
                val event = ServerDisconnectEvent(
                    this.server.id, this.server.javaClass.typeName, reason
                )

                event.push()
            }
    }
}
