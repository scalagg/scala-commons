/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command.data.parameter.impl

import net.evilblock.cubed.command.data.parameter.ParameterType
import org.bukkit.OfflinePlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.ArrayList
import org.bukkit.command.CommandSender

class OfflinePlayerParameterType : ParameterType<OfflinePlayer> {

    override fun transform(sender: CommandSender, source: String): OfflinePlayer {
        return if (sender is Player && (source.equals("self", ignoreCase = true) || source == "")) {
            sender
        } else Bukkit.getServer().getOfflinePlayer(source)
    }

    override fun tabComplete(sender: Player, flags: Set<String>, source: String): List<String> {
        val completions = ArrayList<String>()

        for (player in Bukkit.getOnlinePlayers()) {
            completions.add(player.name)
        }

        return completions
    }

}