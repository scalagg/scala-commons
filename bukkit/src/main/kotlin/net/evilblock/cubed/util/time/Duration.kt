/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.time

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Duration(private var time: Long) {

    fun get(): Long {
        return time
    }

    fun isPermanent(): Boolean {
        return time == Long.MAX_VALUE
    }

    companion object {
        @JvmStatic
        val NOT_PROVIDED = Duration(-1L)

        @JvmStatic
        fun parse(input: String): Duration {
            if (input == "NOT_PROVIDED") {
                return NOT_PROVIDED
            }

            if (input.equals("perm", ignoreCase = true) || input.equals("permanent", ignoreCase = true)) {
                return Duration(Long.MAX_VALUE)
            }

            return Duration(
                DateUtil.parseDuration(
                    input
                )
            )
        }
    }
}