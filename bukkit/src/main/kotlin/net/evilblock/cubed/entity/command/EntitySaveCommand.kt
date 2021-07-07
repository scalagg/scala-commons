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
import org.bukkit.command.CommandSender

object EntitySaveCommand {

    @Command(
        names = ["entity save"],
        description = "Saves all entities to the disk",
        permission = "cubed.entities.save",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        EntityHandler.saveData()
        sender.sendMessage("${ChatColor.GREEN}${ChatColor.BOLD}Saved entities!")
    }

}