/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command.data.parameter.impl

import org.bukkit.entity.Player
import org.bukkit.command.CommandSender
import net.evilblock.cubed.command.data.parameter.ParameterType

class StringParameterType : ParameterType<String> {

    override fun transform(sender: CommandSender, source: String): String {
        return source
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return listOf()
    }

}