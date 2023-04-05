package gg.scala.commons.agnostic.sync.server

import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.agnostic.sync.ServerSyncPipeline
import gg.scala.commons.agnostic.sync.event.events.ServerConnectedEvent
import gg.scala.commons.agnostic.sync.event.events.ServerDisconnectEvent
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 8/29/2022
 */
class SelfServerService<T : AbstractServer>(
    private val server: T,
    private val updateMeta: (T) -> Unit,
    private val serializer: (T) -> String
)
{
    private val keyspace = "${ServerSyncPipeline.NAMESPACE}:servers:${this.server.id}"

    fun configure(): CompletionStage<Void>
    {
        this.updateMeta.invoke(this.server)

        return CompletableFuture
            .supplyAsync {
                ServerContainer.container.sync()
                    .setex(
                        keyspace, 5L,
                        this.serializer(this.server)
                    )
            }
            .exceptionally {
                it.printStackTrace()
                return@exceptionally null
            }
            .thenRunAsync {
                val event = ServerConnectedEvent(
                    this.server.id, this.server.javaClass.typeName
                )

                event.push()

                ServerSync.scheduleAtFixedRate({
                    this.server.lastHeartbeat = System.currentTimeMillis()
                    this.updateMeta.invoke(this.server)

                    ServerContainer.container.sync()
                        .setex(
                            keyspace, 5L, this.serializer(this.server)
                        )
                }, 0L, 500L, TimeUnit.MILLISECONDS)
            }
    }

    fun disconnect(reason: String = "unknown"): CompletionStage<Void>
    {
        return CompletableFuture
            .supplyAsync {
                ServerContainer.container.sync()
                    .set(keyspace, this.serializer(this.server))
            }
            .exceptionally {
                Logger.getGlobal().log(
                    Level.SEVERE, "Failed to update server through SelfServerService", it
                )
                return@exceptionally null
            }
            .thenRunAsync {
                val event = ServerDisconnectEvent(
                    this.server.id, this.server.javaClass.typeName, reason
                )

                event.push()
            }
    }
}
