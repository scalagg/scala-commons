package gg.scala.commons.agnostic.sync.server.event

import gg.scala.aware.message.AwareMessage
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.commons.agnostic.sync.ServerSyncPipeline
import gg.scala.commons.agnostic.sync.event.EventBusSubscriber
import net.evilblock.cubed.serializers.Serializers
import java.util.logging.Level
import java.util.logging.Logger

interface ServerEvent
{
    val id: String
    val type: String

    fun push(local: Boolean = false)
    {
        kotlin.runCatching {
            val message = AwareMessage.of(
                "event-bus",
                ServerSyncPipeline.usePipeline { this },
                "event" to Serializers.gson.toJson(this),
                "type" to this::class.java.name
            )

            if (local)
            {
                EventBusSubscriber.onEventBus(message)
                return
            }

            message.publish(
                context = AwareThreadContext.SYNC
            )
        }.onFailure {
            Logger.getGlobal().log(
                Level.SEVERE, "Failed to push event through EventBus", it
            )
        }
    }
}
