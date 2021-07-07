/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed

object CubedConfig {

    fun getRedisURI(): String {
        return Cubed.instance.config.getString("redis-connection-uri", "redis://localhost:6379?db=0")
    }

    fun getMongoURI(): String {
        return Cubed.instance.config.getString("mongo-connection-uri", "mongodb://localhost:27017")
    }

}