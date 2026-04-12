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

class SelfServerService<T : AbstractServer>(
    private val server: T,
    private val updateMeta: (T) -> Unit,
    private val serializer: (T) -> String
)
{
    private val keyspace = "${ServerSyncPipeline.NAMESPACE}:serversv2"
    private val heartbeatKeyspace = "${ServerSyncPipeline.NAMESPACE}:heartbeats"

    fun configure(): CompletionStage<Void>
    {
        this.updateMeta.invoke(this.server)

        return CompletableFuture
            .supplyAsync {
                ServerContainer.container.sync()
                    .hset(
                        keyspace, this.server.id, this.serializer(this.server)
                    )
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
                        .hset(
                            keyspace, this.server.id, this.serializer(this.server)
                        )

                    this.scheduleNamespaceExpiration()
                }, 0L, 500L, TimeUnit.MILLISECONDS)
            }
            .exceptionally {
                it.printStackTrace()
                return@exceptionally null
            }
    }

    private fun scheduleNamespaceExpiration()
    {
        kotlin.runCatching {
            ServerContainer.container.sync().hset(
                heartbeatKeyspace, this.server.id,
                "${System.currentTimeMillis() + Duration.ofSeconds(5L).toMillis()}"
            )
        }.onFailure {
            Logger.getGlobal().log(
                Level.SEVERE, "Failed to update expiration for self server service", it
            )
        }
    }

    fun disconnect(reason: String = "unknown"): CompletionStage<Void>
    {
        return CompletableFuture
            .supplyAsync {
                ServerContainer.container.sync()
                    .hdel(
                        keyspace, this.server.id
                    )
            }
            .thenRunAsync {
                ServerContainer.container.sync()
                    .hdel(
                        heartbeatKeyspace, this.server.id
                    )
            }
            .thenRunAsync {
                val event = ServerDisconnectEvent(
                    this.server.id, this.server.javaClass.typeName, reason
                )

                event.push()
            }
            .exceptionally {
                Logger.getGlobal().log(
                    Level.SEVERE, "Failed to update server through SelfServerService", it
                )
                return@exceptionally null
            }
    }
}
