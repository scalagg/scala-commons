package net.evilblock.cubed

import net.evilblock.cubed.store.mongo.Mongo
import net.evilblock.cubed.store.redis.Redis
import net.evilblock.cubed.store.uuidcache.UUIDCache
import net.evilblock.cubed.store.uuidcache.impl.RedisUUIDCache
import net.evilblock.cubed.util.BungeeConfigUtils
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File

class Cubed : Plugin() {

    companion object {
        @JvmStatic
        lateinit var instance: Cubed
    }

    lateinit var config: Configuration
    lateinit var uuidCache: UUIDCache

    val redis: Redis = Redis()
    val mongo: Mongo = Mongo()

    override fun onLoad() {
        instance = this
    }

    override fun onEnable() {
        BungeeConfigUtils.saveDefaultConfig(this)

        loadConfig()
        loadStore()
    }

    override fun onDisable() {

    }

    private fun loadConfig() {
        config = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(File(dataFolder, "config.yml"))
    }

    private fun loadStore() {
        try {
            redis.connect(CubedConfig.getRedisURI())
        } catch (e: Exception) {
            throw RuntimeException("Couldn't connect to redis server at ${CubedConfig.getRedisURI()}", e)
        }

        logger.info("Connected to redis server")

        try {
            mongo.connect(CubedConfig.getMongoURI())
        } catch (e: Exception) {
            throw RuntimeException("Couldn't connect to mongodb server at ${CubedConfig.getMongoURI()}", e)
        }

        logger.info("Connected to mongodb server")

        try {
            uuidCache = RedisUUIDCache(redis)
            uuidCache.load()
        } catch (e: Exception) {
            throw RuntimeException("Failed to load UUID cache", e)
        }
    }

}