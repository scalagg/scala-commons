package gg.scala.commons.agnostic.sync.event

import gg.scala.aware.message.AwareMessage
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.commons.agnostic.sync.ServerSyncPipeline

/**
 * @author GrowlyX
 * @since 8/29/2022
 */
interface ServerEvent
{
    val id: String
    val type: String

    fun push()
    {
        AwareMessage.of(
            "event-bus",
            ServerSyncPipeline.usePipeline { this },
            "event" to this,
            "type" to this::class.simpleName
        ).publish(
            context = AwareThreadContext.SYNC
        )
    }
}
