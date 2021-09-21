package gg.scala.banana.message

import gg.scala.banana.helper.BananaHelper

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

    fun convert(key: String, any: Any) {
        keyValueStore[key] = BananaHelper.banana.options.gson.toJson(any)
    }

    fun dispatch() {
        this.dispatch(BananaHelper.banana.options.channel)
    }

    fun dispatch(channel: String) {
        BananaHelper.banana.useResource {
            it.publish(
                channel, BananaHelper.banana.options.gson.toJson(this)
            )
            it.close()
        }
    }
}
