package gg.scala.commons.agnostic.sync.server.event

import gg.scala.aware.message.AwareMessage
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.commons.agnostic.sync.ServerSyncPipeline
import gg.scala.commons.agnostic.sync.event.EventBusPipeline

/**
 * @author GrowlyX
 * @since 8/29/2022
 */
interface ServerEvent
{
    val id: String
    val type: String

    fun push(local: Boolean = false)
    {
        val message = AwareMessage.of(
            "event-bus",
            ServerSyncPipeline.usePipeline { this },
            "event" to this,
            "type" to this::class.java.name
        )

        if (local)
        {
            EventBusPipeline.onEventBus(message)
            return
        }

        message.publish(
            context = AwareThreadContext.SYNC
        )
    }
}
