/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.store.uuidcache.impl

import gg.scala.banana.Banana
import gg.scala.banana.message.Message
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.store.uuidcache.UUIDCache
import net.evilblock.cubed.store.uuidcache.listener.UUIDUpdateHandler
import java.util.*

class RedisUUIDCache(
    private val banana: Banana
) : UUIDCache() {

    init {
        banana.registerClass(
            UUIDUpdateHandler(this)
        )
        banana.subscribe()
    }

    companion object {
        private const val KEY = "cubed:uuid_cache"
    }

    override fun fetchCache(): Map<UUID, String> {
        val cache = hashMapOf<UUID, String>()

        banana.useResource { jedis ->
            jedis.hgetAll(KEY).forEach { (key, value) ->
                try {
                    cache[UUID.fromString(key)] = value
                } catch (ignored: Exception) {
                }
            }
            jedis.close()
        }

        return cache
    }

    override fun fetchUsername(uuid: UUID): String? {
        var reference: String? = null

        banana.useResource { jedis ->
            try {
                reference = jedis.hget(KEY, uuid.toString())
            } catch (ignored: Exception) {
            }
            jedis.close()
        }

        return reference
    }

    override fun fetchUUID(username: String): UUID? {
        var reference: UUID? = null

        banana.useResource { jedis ->
            try {
                reference = UUID.fromString(jedis.hget(KEY, reference.toString()))
            } catch (ignored: Exception) {
            }
            jedis.close()
        }

        return reference
    }

    override fun updateCache(uuid: UUID, name: String) {
        banana.useResource { redis ->
            redis.hset(KEY, uuid.toString(), name)
            redis.hset(KEY, name.lowercase(), uuid.toString())

            redis.publish(
                "cubed",
                Serializers.gson.toJson(
                    Message("uuid_update").also {
                        it["uuid"] = uuid.toString()
                        it["username"] = name
                    }
                )
            )
            redis.close()
        }
    }
}
