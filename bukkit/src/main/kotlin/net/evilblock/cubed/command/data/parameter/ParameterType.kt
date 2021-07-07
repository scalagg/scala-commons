/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command.data.parameter

import org.bukkit.entity.Player
import org.bukkit.command.CommandSender

interface ParameterType<T> {

    fun transform(sender: CommandSender, source: String): T?

    fun tabComplete(player: Player, flags: Set<String>, source: String): List<String>

}
