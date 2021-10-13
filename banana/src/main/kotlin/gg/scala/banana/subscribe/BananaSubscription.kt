package gg.scala.banana.subscribe

import gg.scala.banana.Banana
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

        try {
            val message = banana.options.gson.fromJson(serialized, Message::class.java)
                ?: throw RuntimeException("Incoming message was null")

            val handlers = banana.subscriptions[message.packet]
                ?: if (banana.options.ignoreNonExistentHandlers)
                    return else throw RuntimeException("No handler found.")

            handlers
                .sortedByDescending { it.subscribe.priority }
                .forEach {
                    it.method.invoke(it.clazz, message)
                }
        } catch (exception: Exception) {
            banana.options.exceptionHandler.onIncomingPacket(
                "N/A", exception
            )
        }
    }
}
