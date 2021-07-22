/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.store.uuidcache.impl

import net.evilblock.cubed.store.uuidcache.UUIDCache
import net.evilblock.cubed.store.redis.Redis
import java.util.*

class RedisUUIDCache(private val redis: Redis) : UUIDCache() {

    companion object {
        private const val KEY = "Cubed:UUIDCache"
    }

    override fun fetchCache(): Map<UUID, String> {
        return redis.runRedisCommand { redis -> redis.hgetAll(KEY) }
            .mapNotNull { entry ->
                return@mapNotNull try {
                    UUID.fromString(entry.key) to entry.value
                } catch (e: Exception) {
                    null
                }
            }
            .toMap()
    }

    override fun fetchUsername(uuid: UUID): String? {
        return redis.runRedisCommand { redis ->
            redis.hget(KEY, uuid.toString())
        }
    }

    override fun fetchUUID(username: String): UUID? {
        return redis.runRedisCommand { redis ->
            try {
                UUID.fromString(redis.hget(KEY, username.toLowerCase()))
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun updateCache(uuid: UUID, name: String) {
        redis.runRedisCommand { redis ->
            redis.hset(KEY, uuid.toString(), name)
            redis.hset(KEY, name.toLowerCase(), uuid.toString())
        }
    }
}