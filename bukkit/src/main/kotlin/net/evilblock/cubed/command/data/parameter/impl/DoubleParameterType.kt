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

class DoubleParameterType : ParameterType<Double?> {

    override fun transform(sender: CommandSender, source: String): Double? {
        try {
            val parsed = java.lang.Double.parseDouble(source.replace(",", "").replace("_", ""))

            if (java.lang.Double.isNaN(parsed) || !java.lang.Double.isFinite(parsed)) {
                sender.sendMessage("${ChatColor.RED}$source is not a valid number.")
                return null
            }

            return parsed
        } catch (exception: NumberFormatException) {
            sender.sendMessage("${ChatColor.RED}$source is not a valid number.")
            return null
        }

    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return listOf()
    }

}