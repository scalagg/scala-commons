package gg.scala.commons.agnostic.sync.event

import gg.scala.aware.annotation.Subscribe
import gg.scala.aware.message.AwareMessage
import gg.scala.commons.agnostic.sync.server.event.ServerEvent
import net.evilblock.cubed.serializers.Serializers

/**
 * @author GrowlyX
 * @since 8/29/2022
 */
object EventBusPipeline
{
    @Subscribe("event-bus")
    fun onEventBus(message: AwareMessage)
    {
        val clazz = Class
            .forName(
                message.retrieve("type")
            )

        val event = Serializers.gson.fromJson(
            message.retrieve<String>("event"), clazz
        )

        EventBus.subscribers[clazz.name]
            ?.apply {
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
