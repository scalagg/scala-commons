/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.menus

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class SelectItemStackMenu(
    private val title: String = "Select an item...",
    internal val select: (ItemStack) -> Unit
) : Menu() {

    override fun getTitle(player: Player): String {
        return title
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[4] = GuideButton()

        return buttons
    }

    private inner class GuideButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Select an item..."
        }

        override fun getDescription(player: Player): List<String> {
            return listOf("${ChatColor.GRAY}Click on an item in your inventory to select it.")
        }

        override fun getMaterial(player: Player): XMaterial
        {
            return XMaterial.COMPASS
        }
    }

}
