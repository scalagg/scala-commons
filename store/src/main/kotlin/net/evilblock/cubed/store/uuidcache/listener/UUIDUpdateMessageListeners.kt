package net.evilblock.cubed.store.uuidcache.listener

import com.google.gson.JsonObject
import net.evilblock.cubed.store.uuidcache.UUIDCache
import net.evilblock.pidgin.message.handler.IncomingMessageHandler
import net.evilblock.pidgin.message.listener.MessageListener
import java.util.*

/**
 * Default message listener implementations for syncing UUID cache across multiple cache instances.
 */
class UUIDUpdateMessageListeners(private val uuidCache: UUIDCache) : MessageListener {

    @IncomingMessageHandler(id = "UUIDUpdate")
    fun onUUIDUpdate(data: JsonObject) {
        val uuid = UUID.fromString(data["UUID"].asString)
        val name = data["Name"].asString

        uuidCache.update(uuid, name, commit = false)
    }

}