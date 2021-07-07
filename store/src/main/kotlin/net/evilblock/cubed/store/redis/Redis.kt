/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.store.redis

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import java.io.Closeable
import java.net.URI
import java.util.logging.Logger

/**
 * An easy-to-use utility used for instantiating a connection to a
 * redis server, and executing commands on said server.
 */
class Redis : Closeable {

    var jedisPool: JedisPool? = null

    fun connect(uri: String) {
        try {
            jedisPool = JedisPool(URI(uri), TIMEOUT)
        } catch (e: Exception) {
            Logger.getGlobal().severe("Couldn't connect to redis server at $uri")
            e.printStackTrace()
        }
    }

    /**
     * Close any open Jedis connections.
     */
    override fun close() {
        if (jedisPool != null && !jedisPool!!.isClosed) {
            jedisPool!!.close()
        }
    }

    /**
     * A functional method for using a pooled [Jedis] resource and returning data.
     *
     * @param lambda the function
     */
    fun <T> runRedisCommand(lambda: (Jedis) -> T): T {
        if (jedisPool == null || jedisPool!!.isClosed) {
            throw IllegalStateException("A connection to the redis server couldn't be established or has been forcefully closed")
        }

        try {
            jedisPool!!.resource.use { jedis -> return lambda(jedis) }
        } catch (e: Exception) {
            throw RuntimeException("Could not use resource and return", e)
        }
    }

    companion object {
        private const val TIMEOUT = 5000
    }

}