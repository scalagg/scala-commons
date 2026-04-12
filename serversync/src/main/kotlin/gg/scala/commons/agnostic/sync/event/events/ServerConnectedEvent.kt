package gg.scala.commons.agnostic.sync.event.events

import gg.scala.commons.agnostic.sync.server.event.ServerEvent

data class ServerConnectedEvent(
    override val id: String,
    override val type: String
) : ServerEvent
