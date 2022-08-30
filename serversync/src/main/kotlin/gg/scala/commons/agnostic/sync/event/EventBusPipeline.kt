package gg.scala.commons.agnostic.sync.event

import gg.scala.aware.annotation.Subscribe
import gg.scala.aware.message.AwareMessage
import gg.scala.commons.agnostic.sync.server.event.ServerEvent

/**
 * @author GrowlyX
 * @since 8/29/2022
 */
object EventBusPipeline
{
    @Subscribe("event-bus")
    fun onEventBus(message: AwareMessage)
    {
        val kClass = Class
            .forName(
                message.retrieve("type")
            )
            .kotlin

        val event = message.retrieve("event", kClass) as ServerEvent

        EventBus.subscribers[kClass.simpleName]?.apply {
            for (eventSubscriber in this)
            {
                val continueOrNot = eventSubscriber.filters
                    .none {
                        !it.invoke(event)
                    }

                if (continueOrNot)
                {
                    for (subscriber in eventSubscriber.subscribers)
                    {
                        subscriber.invoke(event)
                    }
                }
            }
        }
    }
}
