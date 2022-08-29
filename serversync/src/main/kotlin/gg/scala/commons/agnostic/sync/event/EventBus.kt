package gg.scala.commons.agnostic.sync.event

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 8/29/2022
 */
object EventBus
{
    val subscribers = ConcurrentHashMap<String, MutableList<EventSubscriber>>()

    inline fun <reified T : ServerEvent> subscribe(): EventSubscriberBuilder<T>
    {
        return EventSubscriberBuilder(T::class)
    }

    class EventSubscriberBuilder<T : ServerEvent>(
        private val kClass: KClass<T>
    )
    {
        private val filters = mutableListOf<(T) -> Boolean>()

        fun filter(lambda: (T) -> Boolean) = this
            .apply {
                this.filters += lambda
            }

        fun listen(lambda: (T) -> Unit) = this
            .let {
                val subscriber = EventSubscriber(
                    listOf {
                        lambda.invoke(it as T)
                    },
                    this.filters
                        .map {
                            { key -> it.invoke(key as T) }
                        }
                )

                subscribers.putIfAbsent(kClass.simpleName!!, mutableListOf())
                subscribers[kClass.simpleName]!!.add(subscriber)
                return@let
            }
    }
}
