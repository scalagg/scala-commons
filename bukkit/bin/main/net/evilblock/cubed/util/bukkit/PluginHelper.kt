/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.bukkit

import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import kotlin.Exception

object PluginHelper {

    @JvmStatic
    fun getProvidingPlugin(clazz: Class<*>): Plugin {
        try {
            return JavaPlugin.getProvidingPlugin(clazz)
        } catch (ignore: Exception) {}

        val cl = clazz.classLoader

        try {
            for (field in cl::class.java.declaredFields) {
                if (field.type == Plugin::class.java) {
                    field.isAccessible = true
                    return field.get(cl) as Plugin
                }
            }
        } catch (ignore: Exception) { }

        throw RuntimeException("Couldn't find plugin instance from declaring class")
    }

}