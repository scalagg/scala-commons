package gg.scala.commons.agnostic.sync.event.events

import gg.scala.commons.agnostic.sync.event.ServerEvent

/**
 * @author GrowlyX
 * @since 8/29/2022
 */
data class ServerConnectedEvent(
    override val id: String,
    override val type: String
) : ServerEvent
