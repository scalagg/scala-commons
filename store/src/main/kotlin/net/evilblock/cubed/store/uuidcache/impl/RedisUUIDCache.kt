/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.store.uuidcache.impl

import gg.scala.banana.Banana
import net.evilblock.cubed.store.uuidcache.UUIDCache
import java.util.*

class RedisUUIDCache(
    private val banana: Banana
) : UUIDCache() {

    companion object {
        private const val KEY = "cubed:uuid_cache"
    }

    override fun fetchCache(): Map<UUID, String> {
        val cache = hashMapOf<UUID, String>()

        banana.useResource { jedis ->
            run {
                jedis.hgetAll(KEY).forEach { (key, value) ->
                    try {
                        cache[UUID.fromString(key)] = value
                    } catch (ignored: Exception) {
                    }
                }
            }
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
        }

        return reference
    }

    override fun updateCache(uuid: UUID, name: String) {
        banana.useResource { redis ->
            redis.hset(KEY, uuid.toString(), name)
            redis.hset(KEY, name.toLowerCase(), uuid.toString())
        }
    }
}
