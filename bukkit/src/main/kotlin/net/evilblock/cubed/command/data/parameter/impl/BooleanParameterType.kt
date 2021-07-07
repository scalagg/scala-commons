/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command.data.parameter.impl

import java.util.ArrayList
import org.bukkit.entity.Player
import org.bukkit.command.CommandSender
import net.evilblock.cubed.command.data.parameter.ParameterType
import org.bukkit.ChatColor

class BooleanParameterType : ParameterType<Boolean?> {

    private val map: MutableMap<String, Boolean> = mutableMapOf()

    init {
        map["on"] = true
        map["off"] = false

        map["yes"] = true
        map["no"] = false

        map["true"] = true
        map["false"] = false
    }

    override fun transform(sender: CommandSender, source: String): Boolean? {
        if (!this.map.containsKey(source.toLowerCase())) {
            sender.sendMessage("${ChatColor.RED} $source is not a valid boolean.")
            return null
        }

        return this.map[source.toLowerCase()]
    }

    override fun tabComplete(sender: Player, flags: Set<String>, source: String): List<String> {
        return ArrayList(this.map.keys)
    }

}