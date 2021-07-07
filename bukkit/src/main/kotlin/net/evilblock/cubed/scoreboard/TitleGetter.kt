/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.scoreboard

import org.bukkit.entity.Player

interface TitleGetter {

    fun getTitle(player: Player): String

    companion object {
        @JvmStatic
        fun ofStatic(title: String): TitleGetter {
            return object : TitleGetter {
                override fun getTitle(player: Player): String {
                    return title
                }
            }
        }
    }

}