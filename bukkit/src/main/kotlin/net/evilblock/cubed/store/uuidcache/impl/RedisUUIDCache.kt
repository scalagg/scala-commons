/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.store.uuidcache.impl

import com.solexgames.lib.util.atomic.AtomicString
import com.solexgames.lib.util.atomic.AtomicUUID
import com.solexgames.redis.JedisManager
import net.evilblock.cubed.store.uuidcache.UUIDCache
import java.util.*

class RedisUUIDCache(private val redis: JedisManager) : UUIDCache() {

    companion object {
        private const val KEY = "Cubed:UUIDCache"
    }

    override fun fetchCache(): Map<UUID, String> {
        val cache = hashMapOf<UUID, String>()

        redis.runCommand { jedis ->
            run {
                jedis.hgetAll(KEY).forEach { (key, value) ->
                    try {
                        cache[UUID.fromString(key)] = value
                    } catch (ignored: Exception) { }
                }
            }
        }

        return cache
    }

    override fun fetchUsername(uuid: UUID): String? {
        val username = AtomicString()

        redis.runCommand { jedis ->
            run {
                try {
                    username.string = jedis.hget(KEY, uuid.toString())
                } catch (ignored: Exception) { }
            }
        }

        return username.string
    }

    override fun fetchUUID(username: String): UUID? {
        val uuid = AtomicUUID()

        redis.runCommand { jedis ->
            run {
                try {
                    uuid.uuid = UUID.fromString(jedis.hget(KEY, uuid.toString()))
                } catch (ignored: Exception) { }
            }
        }

        return uuid.uuid
    }

    override fun updateCache(uuid: UUID, name: String) {
        redis.runCommand { redis ->
            redis.hset(KEY, uuid.toString(), name)
            redis.hset(KEY, name.toLowerCase(), uuid.toString())
        }
    }
}