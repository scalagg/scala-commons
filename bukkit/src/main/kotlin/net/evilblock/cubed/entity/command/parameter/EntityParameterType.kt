/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.command.parameter

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class EntityParameterType : ParameterType<Entity?> {

    override fun transform(sender: CommandSender, source: String): Entity? {
        try {
            return EntityHandler.getEntityByUuid(UUID.fromString(source))
        } catch (e: Exception) {}

        try {
            return EntityHandler.getEntityById(source.toInt())
        } catch (e: Exception) {}

        sender.sendMessage("${ChatColor.RED}Couldn't find an entity by that ID.")
        return null
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return emptyList()
    }

}