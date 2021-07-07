/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.nametag.command

import net.evilblock.cubed.command.Command
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object NametagDebugCommand {

    @Command(
        names = ["cubed nametag debug"],
        description = "View nametag debug",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        if (player.scoreboard == null) {
            player.sendMessage("${ChatColor.RED}No scoreboard attached!")
        } else {
            for (team in player.scoreboard.teams) {
                val entries = team.entries
                player.sendMessage("${ChatColor.YELLOW}${ChatColor.BOLD}Team ${team.name} ${ChatColor.GRAY}(${entries.size})")

                var last = true
                val print = entries.joinToString(transform = {
                    last = !last
                    if (last) {
                        "${ChatColor.DARK_GRAY}$it"
                    } else {
                        "${ChatColor.GRAY}$it"
                    }
                })

                player.sendMessage("${ChatColor.DARK_AQUA}Entries: ${ChatColor.GRAY}$print")
            }
        }
    }

}