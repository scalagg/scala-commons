/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command.data.parameter.impl

import net.evilblock.cubed.command.data.parameter.ParameterType
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PlayerParameterType : ParameterType<Player?> {

    override fun transform(sender: CommandSender, source: String): Player? {
        if (sender is Player && (source.equals("self", true) || source.isEmpty())) {
            return sender
        }

        val player = Bukkit.getServer().getPlayer(source)

        if (player == null) {
            sender.sendMessage("${ChatColor.RED}No player with the name $source found.")
            return null
        }

        return player
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        val completions = ArrayList<String>()

        Bukkit.getOnlinePlayers().forEach { target ->
            completions.add(target.name)
        }

        return completions
    }

}