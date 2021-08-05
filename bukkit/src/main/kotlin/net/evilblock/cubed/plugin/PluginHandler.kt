/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.plugin

import org.bukkit.event.Listener
import java.io.File

abstract class PluginHandler {

    var loaded: Boolean = false

    abstract fun getModule(): PluginModule

    open fun hasDefaultInternalData(): Boolean {
        return false
    }

    open fun getInternalDataFile(): File {
        throw IllegalStateException("Plugin handler does not have internal data")
    }

    open fun initialLoad() {
        if (hasDefaultInternalData()) {
            ensureResourceExists(getInternalDataFile())
        }
    }

    open fun saveData() {
        if (!loaded) {
            throw IllegalStateException("Can't save a handler that hasn't been loaded")
        }
    }

    open fun ensureResourceExists(file: File) {
        file.parentFile.mkdirs()

        if (!file.exists()) {
            try {
                getModule().getPluginFramework().saveResource(File("internal", file.name).path, false)
            } catch (e: Exception) {} // ignore
        }
    }

    open fun getCommands(): List<Class<*>> {
        return emptyList()
    }

    open fun getListeners(): List<Listener> {
        return emptyList()
    }

}