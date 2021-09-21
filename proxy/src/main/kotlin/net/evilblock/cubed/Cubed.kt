package net.evilblock.cubed

import gg.scala.banana.Banana
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

    override fun onLoad() {
        instance = this
    }

    override fun onEnable() {
        BungeeConfigUtils.saveDefaultConfig(this)

        loadConfig()
    }

    private fun loadConfig() {
        config = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(File(dataFolder, "config.yml"))
    }

    fun loadStore(banana: Banana) {
        try {
            uuidCache = RedisUUIDCache(banana)
            uuidCache.load()
        } catch (e: Exception) {
            throw RuntimeException("Failed to load UUID cache", e)
        }
    }

}
