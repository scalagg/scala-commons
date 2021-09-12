/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.store.uuidcache.impl

import com.solexgames.redis.JedisManager
import net.evilblock.cubed.store.uuidcache.UUIDCache
import java.util.*
import java.util.concurrent.atomic.AtomicReference

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
                    } catch (ignored: Exception) {
                    }
                }
            }
        }

        return cache
    }

    override fun fetchUsername(uuid: UUID): String? {
        val reference = AtomicReference<String>()

        redis.runCommand { jedis ->
            run {
                try {
                    reference.set(jedis.hget(KEY, uuid.toString()))
                } catch (ignored: Exception) {
                }
            }
        }

        return reference.get()
    }

    override fun fetchUUID(username: String): UUID? {
        val reference = AtomicReference<UUID>()

        redis.runCommand { jedis ->
            run {
                try {
                    reference.set(UUID.fromString(jedis.hget(KEY, reference.toString())))
                } catch (ignored: Exception) {
                }
            }
        }

        return reference.get()
    }

    override fun updateCache(uuid: UUID, name: String) {
        redis.runCommand { redis ->
            redis.hset(KEY, uuid.toString(), name)
            redis.hset(KEY, name.toLowerCase(), uuid.toString())
        }
    }
}