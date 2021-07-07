package net.evilblock.cubed.util.bukkit

import org.bukkit.ChatColor

object Rainbow : Runnable {

    private var index: Int = 0

    private val options = listOf(
        ChatColor.DARK_RED,
        ChatColor.RED,
        ChatColor.GOLD,
        ChatColor.YELLOW,
        ChatColor.GREEN,
        ChatColor.DARK_GREEN,
        ChatColor.AQUA,
        ChatColor.DARK_AQUA,
        ChatColor.BLUE,
        ChatColor.DARK_BLUE,
        ChatColor.DARK_PURPLE,
        ChatColor.LIGHT_PURPLE
    )

    @JvmStatic
    var currentColor: ChatColor = options.first()

    override fun run() {
        index++

        if (index >= options.size) {
            index = 0
        }

        currentColor = options[index]
    }

}