/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.template.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class TemplatePreviewMenu(private val template: MenuTemplate<*>) : Menu() {

    override fun getTitle(player: Player): String {
        return "Preview of ${template.getName()}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for ((slot, button) in template.boundButtons) {
            buttons[slot] = Button.placeholder(button.getIcon())
        }

        val size = size(buttons)
        for (i in 0 until size) {
            if (!buttons.containsKey(i)) {
                buttons[i] = PlaceholderButton()
            }
        }

        return buttons
    }

    private inner class PlaceholderButton : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            return ItemUtils.applySkullTexture(ItemBuilder.of(Material.SKULL_ITEM).name("${CC.YELLOW}Template Placeholder").data(3).build(), PLACEHOLDER_TEXTURE)
        }
    }

    companion object {
        private const val PLACEHOLDER_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhhNWE1ZDdmMTM1NTRiN2Q4OGQyNzY2ZTlhM2FmZTI3YTY4YTU2MmIxOWNmYzRkODQxYjAxMWZmZmQ3MTkzNiJ9fX0="
    }
}