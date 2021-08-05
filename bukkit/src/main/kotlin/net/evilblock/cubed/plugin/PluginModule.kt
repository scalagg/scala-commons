/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.plugin

import net.evilblock.cubed.logging.ErrorHandler
import org.bukkit.ChatColor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

abstract class PluginModule {

    var config: YamlConfiguration = loadConfig()
    var loaded: Boolean = false

    abstract fun getPluginFramework(): PluginFramework

    fun isEnabled(): Boolean {
        return getPluginFramework().getModules().contains(this)
    }

    private fun getConfigFile(): File {
        return File(getPluginFramework().dataFolder, getConfigFileName() + ".yml")
    }

    private fun loadConfig(): YamlConfiguration {
        val configFile = getConfigFile()
        if (!configFile.exists()) {
            JavaPlugin.getProvidingPlugin(this.javaClass).saveResource(getConfigFileName() + ".yml", false)
        }

        return YamlConfiguration.loadConfiguration(configFile)
    }

    fun saveConfig() {
        config.save(getConfigFile())
    }

    abstract fun getName(): String

    abstract fun getConfigFileName(): String

    open fun onEnable() {
        for (handler in getPluginHandlers()) {
            try {
                handler.initialLoad()
                handler.loaded = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    open fun onDisable() {
        for (handler in getPluginHandlers()) {
            try {
                if (handler.loaded) {
                    handler.saveData()
                }

                handler.loaded = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    open fun onReload() {
        config = loadConfig()
    }

    open fun onAutoSave() {
        for (handler in getPluginHandlers()) {
            try {
                if (handler.loaded) {
                    handler.saveData()
                }
            } catch (e: Exception) {
                val handlerName = handler::class.java.simpleName

                ErrorHandler.generateErrorLog(
                    errorType = "saveHandler",
                    event = mapOf(
                        "ModuleName" to getName(),
                        "HandlerName" to handlerName
                    ),
                    exception = e
                )

                getPluginFramework().systemLog("${ChatColor.RED}Failed to save handler $handlerName!")
            }
        }
    }

    open fun requiresLateLoad(): Boolean {
        return false
    }

    open fun getCommands(): List<Class<*>> {
        return emptyList()
    }

    open fun getListeners(): List<Listener> {
        return emptyList()
    }

    open fun getPluginHandlers(): List<PluginHandler> {
        return emptyList()
    }

}