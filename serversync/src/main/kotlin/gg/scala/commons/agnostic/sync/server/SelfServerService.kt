package gg.scala.commons.agnostic.sync.server

import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.agnostic.sync.ServerSyncPipeline
import gg.scala.commons.agnostic.sync.event.events.ServerConnectedEvent
import gg.scala.commons.agnostic.sync.event.events.ServerDisconnectEvent
import java.time.Duration
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

        return ServerContainer.container.async()
            .set(keyspace, this.serializer(this.server))
            .exceptionally {
                it.printStackTrace()
                return@exceptionally null
            }
            .thenRunAsync {
                this.scheduleNamespaceExpiration()

                val event = ServerConnectedEvent(
                    this.server.id, this.server.javaClass.typeName
                )

                event.push()

                ServerSync.scheduleAtFixedRate({
                    this.server.lastHeartbeat = System.currentTimeMillis()
                    this.updateMeta.invoke(this.server)

                    ServerContainer.container.sync()
                        .set(
                            keyspace, this.serializer(this.server)
                        )

                    this.scheduleNamespaceExpiration()
                }, 0L, 500L, TimeUnit.MILLISECONDS)
            }
    }

    private fun scheduleNamespaceExpiration()
    {
        kotlin.runCatching {
            // manually set expiration in-case server
            // times out in the future
            ServerContainer.container.sync().expire(
                keyspace, Duration.ofSeconds(5L)
            )
        }.onFailure {
            Logger.getGlobal().log(
                Level.SEVERE, "Failed to update expiration for self server service", it
            )
        }
    }

    fun disconnect(reason: String = "unknown"): CompletionStage<Void>
    {
        return ServerContainer.container.async()
            .del(this.keyspace)
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
