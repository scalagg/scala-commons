package net.evilblock.cubed.store.uuidcache.listener

import gg.scala.banana.annotate.Subscribe
import gg.scala.banana.message.Message
import gg.scala.banana.subscribe.marker.BananaHandler
import net.evilblock.cubed.store.uuidcache.UUIDCache
import java.util.*

/**
 * Default message listener implementations for syncing UUID cache across multiple cache instances.
 */
class UUIDUpdateHandler(
    private val uuidCache: UUIDCache
) : BananaHandler {

    @Subscribe("UUIDUpdate")
    fun onUUIDUpdate(data: Message) {
        val uuid = UUID.fromString(data["uuid"])
        val name = data["Name"]

        if (name != null) {
            uuidCache.update(uuid, name, commit = false)
        }
    }

}
