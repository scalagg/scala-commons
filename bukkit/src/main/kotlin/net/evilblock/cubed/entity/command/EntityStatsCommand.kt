/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.text.NumberFormat

object EntityStatsCommand {

    @Command(
        names = ["entity stats"],
        description = "Shows entity stats",
        permission = "cubed.entities.stats"
    )
    @JvmStatic
    fun execute(player: Player) {
        player.sendMessage("")
        player.sendMessage("${ChatColor.GREEN}Printing stats for entities...")
        player.sendMessage("")
        player.sendMessage("${ChatColor.GOLD}Current ID: ${Entity.currId()}")
        player.sendMessage("${ChatColor.GOLD}Tracked entities: ${NumberFormat.getInstance().format(EntityHandler.getEntities().size)}")
        player.sendMessage("")
    }

}