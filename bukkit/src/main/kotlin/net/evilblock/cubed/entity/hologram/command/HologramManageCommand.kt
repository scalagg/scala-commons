/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.hologram.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.cubed.entity.menu.EditEntityMenu
import net.evilblock.cubed.entity.tool.EntitySelectionTool
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object HologramManageCommand {

    @Command(
        names = ["hologram manage", "holo manage"],
        description = "Manage a hologram",
        permission = "cubed.holograms.manage",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        player.sendMessage("${ChatColor.GREEN}Select (right-click) a hologram to manage...")

        EntitySelectionTool.attachSelectionHandler(player) { entity ->
            if (entity is HologramEntity) {
                Tasks.sync {
                    EditEntityMenu(entity).openMenu(player)
                }
            }
        }
    }

}