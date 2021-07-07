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
import net.evilblock.cubed.entity.tool.EntityPortTool
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object EntityImportCommand {

    @Command(
        names = ["entity import"],
        description = "Import entities from a file",
        permission = "cubed.entities.import",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "fileName") fileName: String) {
        val file = EntityPortTool.findImportDataFile(fileName)
        if (!file.exists()) {
            player.sendMessage("${ChatColor.RED}Couldn't find file: ${file.path}")
            return
        }

        val entities = EntityPortTool.import(file)
        for (entity in entities) {
            entity.updateLocation(player.location)
            EntityHandler.trackEntity(entity)
        }

        EntityHandler.saveData()

        player.sendMessage("${ChatColor.GREEN}Successfully imported ${entities.size} from ${file.path}!")
    }

}