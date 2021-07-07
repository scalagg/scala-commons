/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.entity.tool.EntityPortTool
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object EntityExportCommand {

    @Command(
        names = ["entity export"],
        description = "Export a selection of entities",
        permission = "cubed.entities.export",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        if (EntityPortTool.isTracking(player)) {
            EntityPortTool.stopTracking(player)
            player.sendMessage("${ChatColor.RED}")
        } else {
            EntityPortTool.startTracking(player)
            player.sendMessage("${ChatColor.GREEN}Starting export selection... Right-click any hologram/NPC to mark for export!")
            player.sendMessage("${ChatColor.GREEN}Shift left-click while sneaking to finish export!")
        }
    }

}