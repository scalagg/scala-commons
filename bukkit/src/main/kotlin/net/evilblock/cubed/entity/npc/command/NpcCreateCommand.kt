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
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import kotlin.math.floor

object NpcCreateCommand {

    @Command(
        names = ["npc create"],
        description = "Spawn an NPC",
        permission = "cubed.npcs.create",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "text", wildcard = true) text: String) {
        if (text.length > 48) {
            player.sendMessage("${ChatColor.RED}Text cannot be longer than 48 characters.")
            return
        }

        val loc = player.location
        loc.x = floor(loc.x) + 0.5
        loc.z = floor(loc.z) + 0.5

        val npc = NpcEntity(listOf(ChatColor.translateAlternateColorCodes('&', text)), loc)
        npc.initializeData()
        npc.spawn(player)

        EntityHandler.trackEntity(npc)
        EntityHandler.saveData()

        player.sendMessage("${ChatColor.GREEN}Successfully created NPC!")
    }

}