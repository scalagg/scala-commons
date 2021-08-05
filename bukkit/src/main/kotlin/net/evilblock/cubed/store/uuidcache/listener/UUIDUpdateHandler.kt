package net.evilblock.cubed.store.uuidcache.listener

import com.solexgames.redis.annotation.Subscription
import com.solexgames.redis.handler.JedisHandler
import com.solexgames.redis.json.JsonAppender
import net.evilblock.cubed.store.uuidcache.UUIDCache
import java.util.*

/**
 * Default message listener implementations for syncing UUID cache across multiple cache instances.
 */
class UUIDUpdateHandler(private val uuidCache: UUIDCache) : JedisHandler {

    @Subscription(action = "UUIDUpdate")
    fun onUUIDUpdate(data: JsonAppender) {
        val uuid = UUID.fromString(data.getParam("uuid"))
        val name = data.getParam("Name")

        uuidCache.update(uuid, name, commit = false)
    }

}