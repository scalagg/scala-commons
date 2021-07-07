/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.npc.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.math.Numbers
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object NpcDeleteNearCommand {

    @Command(
        names = ["npc delete-near"],
        description = "Deletes NPCs within a radius of blocks",
        permission = "cubed.npcs.delete"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "radius", defaultValue = "5") radius: Int) {
        val inRadius = arrayListOf<NpcEntity>()
        for (npc in EntityHandler.getEntities().filterIsInstance<NpcEntity>()) {
            if (player.world == npc.location.world && player.location.distanceSquared(npc.location) <= radius) {
                inRadius.add(npc)
            }
        }

        if (inRadius.isEmpty()) {
            player.sendMessage("${ChatColor.RED}There aren't any NPCs nearby!")
            return
        }

        ConfirmMenu("Delete ${Numbers.format(inRadius.size)} NPCs?") { confirmed ->
            if (confirmed) {
                Tasks.async {
                    for (npc in inRadius) {
                        EntityHandler.forgetEntity(npc)
                        npc.destroyForCurrentWatchers()
                    }
                }

                player.sendMessage("${ChatColor.GREEN}Successfully deleted ${Numbers.format(inRadius.size)} NPCs!")
            }
        }.openMenu(player)
    }

}