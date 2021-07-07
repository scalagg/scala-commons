package net.evilblock.cubed.util

import com.google.common.io.ByteStreams
import net.md_5.bungee.api.plugin.Plugin
import java.io.File
import java.io.FileOutputStream

object BungeeConfigUtils {

    @JvmStatic
    fun saveDefaultConfig(plugin: Plugin) {
        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdir()
        }

        val configFile = File(plugin.dataFolder, "config.yml")
        if (!configFile.exists()) {
            try {
                configFile.createNewFile()

                plugin.getResourceAsStream("bungee_config.yml").use { input ->
                    FileOutputStream(configFile).use { output ->
                        ByteStreams.copy(input, output)
                    }
                }
            } catch (e: Throwable) {
                throw RuntimeException("Unable to create configuration file", e)
            }
        }
    }

}