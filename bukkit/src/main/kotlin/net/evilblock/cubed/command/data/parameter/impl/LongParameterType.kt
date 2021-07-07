/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command.data.parameter.impl

import net.evilblock.cubed.command.data.parameter.ParameterType
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.command.CommandSender

class LongParameterType : ParameterType<Long?> {

    override fun transform(sender: CommandSender, value: String): Long? {
        return try {
            value.replace(",", "").replace("_", "").toLong()
        } catch (exception: NumberFormatException) {
            sender.sendMessage("${ChatColor.RED}$value is not a valid number.")
            null
        }
    }

    override fun tabComplete(sender: Player, flags: Set<String>, prefix: String): List<String> {
        return listOf()
    }

}