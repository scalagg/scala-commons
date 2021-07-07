/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.npc.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.entity.menu.EditEntityMenu
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.entity.tool.EntitySelectionTool
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object NpcManageCommand {

    @Command(
        names = ["npc manage"],
        description = "Manage an NPC",
        permission = "cubed.npcs.manage"
    )
    @JvmStatic
    fun execute(player: Player) {
        player.sendMessage("${ChatColor.GREEN}Select (right-click) an NPC to manage...")

        EntitySelectionTool.attachSelectionHandler(player) { entity ->
            if (entity is NpcEntity) {
                EditEntityMenu(entity).openMenu(player)
            }
        }
    }

}