package gg.scala.banana.subscribe

import gg.scala.banana.Banana
import gg.scala.banana.helper.BananaHelper
import gg.scala.banana.message.Message
import redis.clients.jedis.JedisPubSub

/**
 * Handles all incoming packets from the
 * banana subscription defined in the [Banana]
 * instance
 *
 * @author GrowlyX
 * @since 9/19/2021
 */
class BananaSubscription(
    private val banana: Banana
) : JedisPubSub() {

    override fun onMessage(channel: String?, serialized: String?) {
        if (channel != banana.options.channel) {
            return
        }

        if (banana.options.debug) {
            println("Received message (#1)")
        }

        BananaHelper.handle(
            {
                try {
                    val message = banana.options.gson.fromJson(serialized, Message::class.java)
                        ?: throw RuntimeException("Incoming message was null")
                    val handlers = banana.subscriptions[message.packet] ?: if (!banana.options.ignoreNonExistentHandlers) return@handle else throw RuntimeException("No handler found.")

                    handlers.sortedByDescending { it.subscribe.priority }
                        .forEach {
                            if (banana.options.debug) {
                                println("Handling message ${it.subscribe.value}")
                            }

                            it.method.invoke(it.clazz, message)
                        }
                } catch (exception: Exception) {
                    banana.options.exceptionHandler.onIncomingPacket(
                        "N/A", exception
                    )
                }
            },
            banana
        )
    }
}
