package gg.scala.banana.message

import gg.scala.banana.Banana
import java.util.*

/**
 * @author GrowlyX
 * @since 9/19/2021
 */
class Message(
    val packet: String
) {
    private val keyValueStore = WeakHashMap<String, String>()

    operator fun get(key: String): String? {
        return keyValueStore[key]
    }

    operator fun set(key: String, value: String) {
        keyValueStore[key] = value
    }

    fun dispatch(banana: Banana) {
        this.dispatch(banana.options.channel, banana)
    }

    fun dispatch(channel: String, banana: Banana) {
        banana.useResource {
            it.publish(
                channel, banana.options.gson.toJson(this)
            )
            it.close()
        }
    }
}
