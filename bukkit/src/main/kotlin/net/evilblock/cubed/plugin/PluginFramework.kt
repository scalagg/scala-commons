/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.plugin

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.logging.ErrorHandler
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin

abstract class PluginFramework : JavaPlugin() {

    private var startupFailed: Boolean = false
    private val enabledModules: MutableList<PluginModule> = arrayListOf()

    override fun onEnable() {
        try {
            loadModules()
        } catch (e: Exception) {
            ErrorHandler.generateErrorLog("startup", emptyMap(), e)
            e.printStackTrace()
            startupFailed = true
        }

        if (startupFailed) {
            server.shutdown()
            return
        }

        Tasks.asyncTimer(20L * 60L * 3L, 20L * 60L * 3L) {
            saveModules()
        }
    }

    abstract fun getModules(): List<PluginModule>

    override fun onDisable() {
        if (startupFailed) {
            throw IllegalStateException("${this::class.java.simpleName} failed to initialize")
        } else {
            disableModules()
        }
    }

    fun loadModules() {
        for (module in getModules().filter { !it.requiresLateLoad() }) {
            logger.info("Loading ${module.getName()} module...")

            module.loaded = false
            module.onEnable()

//            module.getCommands().forEach { command ->
//                CommandHandler.registerClass(command)
//            }
//
//            module.getCommandParameterTypes().forEach { param ->
//                CommandHandler.registerParameterType(param.key, param.value)
//            }

            module.getListeners().forEach { listener ->
                server.pluginManager.registerEvents(listener, this)
            }

            module.getPluginHandlers().forEach { handler ->
//                handler.getCommands().forEach { command ->
//                    CommandHandler.registerClass(command)
//                }
//
//                handler.getCommandParameterTypes().forEach { param ->
//                    CommandHandler.registerParameterType(param.key, param.value)
//                }

                handler.getListeners().forEach { listener ->
                    server.pluginManager.registerEvents(listener, this)
                }
            }

            module.loaded = true

            enabledModules.add(module)

            logger.info("Enabled ${module.getName()} module!")
        }

        Tasks.delayed(1L) {
            for (module in getModules().filter { it.requiresLateLoad() }) {
                logger.info("Loading ${module.getName()} module... (late load)")

                try {
                    module.onEnable()
//                    module.getCommands().forEach { command -> CommandHandler.registerClass(command) }
//                    module.getCommandParameterTypes().forEach { parameterEntry -> CommandHandler.registerParameterType(parameterEntry.key, parameterEntry.value) }
                    module.getListeners().forEach { listener -> server.pluginManager.registerEvents(listener, this) }

                    logger.info("Enabled ${module.getName()} module (late load)!")
                } catch (e: Exception) {
                    logger.severe("Failed to enable ${module.getName()} module (late load):")
                    e.printStackTrace()
                }
            }
        }
    }

    fun disableModules() {
        for (module in enabledModules) {
            logger.info("Disabling ${module.getName()} module...")

            try {
                module.onDisable()
                module.loaded = false

                logger.info("Disabled ${module.getName()} module!")
            } catch (e: Exception) {
                logger.severe("Failed to disable ${module.getName()} module:")
                e.printStackTrace()
            }
        }
    }

    fun saveModules() {
        val startAt = System.currentTimeMillis()

        systemLog("${ChatColor.GRAY}Saving plugin data...")

        getModules().forEach { module ->
            try {
                module.onAutoSave()
            } catch (exception: Exception) {
                ErrorHandler.generateErrorLog(
                    errorType = "saveModule",
                    event = mapOf("ModuleName" to module.getName()),
                    exception = exception
                )

                systemLog("${ChatColor.RED}Failed to save module ${module.getName()}!")
            }
        }

        val endAt = System.currentTimeMillis()

        systemLog("${ChatColor.GRAY}Finished system save in ${endAt - startAt}ms!")
    }

    open fun systemLog(vararg messages: String) {
        systemLog(prefix = "${ChatColor.DARK_RED}${ChatColor.BOLD}[$name]", messages = messages)
    }

    open fun getSpawnLocation(): Location {
        return server.worlds[0].spawnLocation
    }

    companion object {
        private val SYSTEM_PREFIX = "${ChatColor.RED}[System]"

        fun systemLog(prefix: String = SYSTEM_PREFIX, vararg messages: String) {
            for (message in messages) {
                Cubed.instance.logger.info("$prefix $message")
            }

            for (player in Bukkit.getOnlinePlayers()) {
                if (player.isOp || player.hasPermission("cubed.system.admin")) {
                    for (message in messages) {
                        player.sendMessage("$prefix ${ChatColor.GRAY}$message")
                    }
                }
            }
        }
    }
}
