package gg.scala.banana.message

import gg.scala.banana.Banana

/**
 * @author GrowlyX
 * @since 9/19/2021
 */
class Message(
    val packet: String
) {
    private val keyValueStore = mutableMapOf<String, String>()

    operator fun get(key: String): String? {
        return keyValueStore[key]
    }

    operator fun set(key: String, value: String) {
        keyValueStore[key] = value
    }

    fun dispatch() {
        this.dispatch(Banana.bananas[Banana.bananas.size - 1].options.channel)
    }

    fun dispatch(channel: String) {
        Banana.bananas[Banana.bananas.size - 1].useResource {
            it.publish(
                channel, Banana.bananas[Banana.bananas.size - 1].options.gson.toJson(this)
            )
            it.close()
        }
    }
}
