package gg.scala.commons.agnostic.sync.event.subscriber

data class EventSubscriber(
    val subscribers: List<(Any) -> Unit>,
    val filters: List<(Any) -> Boolean>
)
