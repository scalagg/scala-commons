/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.entity.EntityHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object EntityNearbyCommand {

    @Command(
        names = ["entity nearby"],
        description = "Lists nearby entities",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "radius", defaultValue = "64.0") radius: Double) {
        val nearby = EntityHandler.getEntities().filter { it.location.world == player.location.world && it.location.distanceSquared(player.location) < radius }
        if (nearby.isEmpty()) {
            player.sendMessage("${ChatColor.RED}There are no nearby entities!")
            return
        }

        for (entity in nearby) {
            player.sendMessage("${entity.id} - ${entity.uuid}")
        }
    }

}