/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.entity.EntityHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object EntityFixNullWorldsCommand {

    @Command(
        names = ["entity fix-null-worlds"],
        description = "Deletes entities with null worlds (debugging)",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        var deleted = 0
        for (entity in EntityHandler.getEntities()) {
            if (entity.location.world == null) {
                deleted++
                EntityHandler.forgetEntity(entity)
                entity.destroyForCurrentWatchers()
            }
        }
        player.sendMessage("${ChatColor.GREEN}Fixed $deleted entities with null worlds!")
    }

}