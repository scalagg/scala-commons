package gg.scala.commons.agnostic.sync.event.subscriber

/**
 * @author GrowlyX
 * @since 8/29/2022
 */
data class EventSubscriber(
    val subscribers: List<(Any) -> Unit>,
    val filters: List<(Any) -> Boolean>
)
