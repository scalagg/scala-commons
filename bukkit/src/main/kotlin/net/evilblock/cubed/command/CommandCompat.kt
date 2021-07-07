/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command

object CommandCompat {

    private val SPIGOT_CONFIG_CLASS = Class.forName("org.spigotmc.SpigotConfig")
    private val UNKNOWN_COMMAND_MESSAGE = SPIGOT_CONFIG_CLASS.getDeclaredField("unknownCommandMessage")

    init {
        UNKNOWN_COMMAND_MESSAGE.isAccessible = true
    }

    fun getUnknownCommandMessage(): String {
        return UNKNOWN_COMMAND_MESSAGE.get(null) as String
    }

}