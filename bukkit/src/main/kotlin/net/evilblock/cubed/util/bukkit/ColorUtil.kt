/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.bukkit

import org.apache.commons.lang.WordUtils
import org.bukkit.ChatColor
import org.bukkit.Color

object ColorUtil {

    private val COLOR_TO_NAME = mapOf<Color, String>(
        Color.WHITE to "WHITE",
        Color.RED to "RED",
        Color.BLUE to "BLUE",
        Color.AQUA to "AQUA",
        Color.PURPLE to "DARK_PURPLE",
        Color.FUCHSIA to "LIGHT_PURPLE",
        Color.ORANGE to "GOLD",
        Color.YELLOW to "YELLOW",
        Color.LIME to "GREEN",
        Color.GREEN to "DARK_GREEN",
        Color.SILVER to "GRAY",
        Color.GRAY to "DARK_GRAY",
        Color.MAROON to "DARK_RED",
        Color.NAVY to "DARK_AQUA",
        Color.BLACK to "BLACK"
    )

    private val COLOR_TO_CHAT_COLOR = mapOf<Color, ChatColor>(
        Color.WHITE to ChatColor.WHITE,
        Color.RED to ChatColor.RED,
        Color.BLUE to ChatColor.BLUE,
        Color.AQUA to ChatColor.AQUA,
        Color.PURPLE to ChatColor.DARK_PURPLE,
        Color.FUCHSIA to ChatColor.LIGHT_PURPLE,
        Color.ORANGE to ChatColor.GOLD,
        Color.YELLOW to ChatColor.YELLOW,
        Color.LIME to ChatColor.GREEN,
        Color.GREEN to ChatColor.DARK_GREEN,
        Color.SILVER to ChatColor.GRAY,
        Color.GRAY to ChatColor.DARK_GRAY,
        Color.MAROON to ChatColor.DARK_RED,
        Color.NAVY to ChatColor.DARK_AQUA,
        Color.BLACK to ChatColor.BLACK
    )

    @JvmStatic
    val CHAT_COLOR_TO_DYE_DATA = mapOf(
        ChatColor.WHITE to 15,
        ChatColor.GOLD to 14,
        ChatColor.AQUA to 12,
        ChatColor.YELLOW to 11,
        ChatColor.GREEN to 10,
        ChatColor.LIGHT_PURPLE to 9,
        ChatColor.GRAY to 8,
        ChatColor.DARK_GRAY to 7,
        ChatColor.DARK_AQUA to 6,
        ChatColor.DARK_PURPLE to 5,
        ChatColor.BLUE to 4,
        ChatColor.DARK_GREEN to 2,
        ChatColor.RED to 1,
        ChatColor.DARK_RED to 1,
        ChatColor.BLACK to 0
    )

    @JvmStatic
    val CHAT_COLOR_TO_WOOL_DATA = mapOf(
        ChatColor.DARK_RED to 14,
        ChatColor.RED to 14,
        ChatColor.GOLD to 1,
        ChatColor.YELLOW to 4,
        ChatColor.GREEN to 5,
        ChatColor.DARK_GREEN to 13,
        ChatColor.DARK_AQUA to 9,
        ChatColor.AQUA to 3,
        ChatColor.BLUE to 11,
        ChatColor.DARK_PURPLE to 10,
        ChatColor.LIGHT_PURPLE to 2,
        ChatColor.WHITE to 0,
        ChatColor.GRAY to 8,
        ChatColor.DARK_GRAY to 7,
        ChatColor.BLACK to 15
    )

    @JvmStatic
    fun getPredefinedColors(): Set<Color> {
        return COLOR_TO_NAME.keys
    }

    @JvmStatic
    fun toChatColor(color: Color): ChatColor {
        return COLOR_TO_CHAT_COLOR[color] ?: ChatColor.WHITE
    }

    @JvmStatic
    fun getColorName(color: Color): String {
        return COLOR_TO_NAME[color] ?: "Custom"
    }

    @JvmStatic
    fun toDyeData(color: ChatColor): Int {
        return CHAT_COLOR_TO_DYE_DATA[color] ?: 0
    }

    @JvmStatic
    fun toWoolData(color: ChatColor): Int {
        return CHAT_COLOR_TO_WOOL_DATA[color] ?: 0
    }

    @JvmStatic
    fun getChatColorByWoolId(data: Byte): ChatColor? {
        for ((key, value) in CHAT_COLOR_TO_WOOL_DATA) {
            if (value == data.toInt()) {
                return key
            }
        }
        return null
    }

    @JvmStatic
    fun getWoolName(data: Byte): String {
        val color = getChatColorByWoolId(data)
        return WordUtils.capitalizeFully((color?.name ?: "UNKNOWN").replace("_", " "))
    }

    @JvmStatic
    fun convertColorValue(value: Double): Double {
        var value = value
        if (value <= 0.0) {
            value = -1.0
        }
        return value / 255.0
    }

}