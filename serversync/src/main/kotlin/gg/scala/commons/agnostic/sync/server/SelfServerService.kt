package gg.scala.commons.agnostic.sync.server

import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.agnostic.sync.ServerSyncPipeline
import gg.scala.commons.agnostic.sync.event.events.ServerConnectedEvent
import gg.scala.commons.agnostic.sync.event.events.ServerDisconnectEvent
import net.evilblock.cubed.serializers.Serializers
import java.time.Duration
import java.util.concurrent.CompletionStage
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

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
        val keyspace = "${ServerSyncPipeline.NAMESPACE}:servers:${this.server.id}"
        this.updateMeta.invoke(this.server)

        return ServerContainer.container.async()
            .set(
                keyspace, Serializers.gson.toJson(this.server)
            )
            .exceptionally {
                it.printStackTrace()
                return@exceptionally null
            }
            .thenRunAsync {
                this.scheduleNamespaceExpiration(keyspace)

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

    private fun scheduleNamespaceExpiration(keyspace: String)
    {
        kotlin.runCatching {
            // manually set expiration in-case server
            // times out in the future
            ServerContainer.container.sync().expire(
                keyspace, Duration.ofSeconds(5L)
            )
        }.onFailure {
            Logger.getLogger("ServerSyncPipeline").log(
                Level.SEVERE, "Failed to update expiration for self server service", it
            )
        }
    }

    fun disconnect(reason: String = "unknown"): CompletionStage<Void>
    {
        return ServerContainer.container.async()
            .hdel(
                ServerSyncPipeline.NAMESPACE, this.server.id
            )
            .exceptionally {
                it.printStackTrace()
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
