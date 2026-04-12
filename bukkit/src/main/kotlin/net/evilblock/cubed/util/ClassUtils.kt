

package net.evilblock.cubed.util

import org.bukkit.plugin.Plugin

object ClassUtils {

    @JvmStatic
    fun getClassesInPackage(plugin: Plugin, packageName: String): Collection<Class<*>> {
        return ClassUtils2.getClassesInPackage(plugin, packageName)
    }
}
