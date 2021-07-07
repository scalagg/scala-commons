/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.entity.Entity
import org.bukkit.entity.Player

object EntityTeleportCommand {

    @Command(
        names = ["entity tp"],
        description = "Teleport to a Hologram by supplying the Hologram's ID",
        permission = "cubed.entities.manage",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "id") entity: Entity) {
        player.teleport(entity.location)
    }

}