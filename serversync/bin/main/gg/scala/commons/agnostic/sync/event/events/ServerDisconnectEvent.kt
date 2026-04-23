package gg.scala.commons.agnostic.sync.event.events

import gg.scala.commons.agnostic.sync.server.event.ServerEvent

/**
 * @author GrowlyX
 * @since 8/29/2022
 */
data class ServerDisconnectEvent(
    override val id: String,
    override val type: String,
    val reason: String = "Closed"
) : ServerEvent
