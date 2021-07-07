/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command

import net.evilblock.cubed.util.Reflection
import org.bukkit.entity.Player
import java.lang.reflect.Method

interface ICommand {

    fun getNames(): Array<String>

    fun getDescription(): String

    fun getPermission(): String

    fun isAsync(): Boolean {
        return false
    }

    fun isHidden(): Boolean {
        return false
    }

    fun isLogToConsole(): Boolean {
        return false
    }

    fun getMethod(): Method {
        return Reflection.getDeclaredMethod(this::class.java, "onCommand", Player::class.java)!!
    }

    fun onCommand(player: Player) {

    }

}