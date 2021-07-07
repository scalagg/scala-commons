/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.nametag.command

import net.evilblock.cubed.command.Command
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object NametagStatsCommand {

    @Command(
        names = ["cubed nametag stats"],
        description = "View nametag update statistics",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        var totalTeams = 0
        var totalEntries = 0

        val scoreboards = Bukkit.getOnlinePlayers().mapNotNull { it.scoreboard }
        for (scoreboard in scoreboards) {
            for (team in scoreboard.teams) {
                totalTeams++
                totalEntries += team.entries.size
            }
        }

        sender.sendMessage("${ChatColor.GOLD}There are ${ChatColor.WHITE}${scoreboards.size} ${ChatColor.GOLD}scoreboards with ${ChatColor.WHITE}${totalTeams} ${ChatColor.GOLD}teams and ${ChatColor.WHITE}${totalEntries} ${ChatColor.GOLD}entries.")
    }

}