/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.template.button

import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.menu.template.MenuTemplateButton
import net.evilblock.cubed.menu.template.MenuTemplateButtonFactory
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type

class TemplatePlaceholderButton : MenuTemplateButton(), CloneableMenuTemplateButton {

    override fun getEditorIcon(): ItemStack {
        return ItemUtils.applySkullTexture(ItemBuilder.of(Material.SKULL_ITEM).name("${CC.GREEN}Template Placeholder").data(3).build(), TEXTURE)
    }

    override fun getEditorActions(): List<ButtonAction> {
        return listOf(
            ButtonAction(clickType = ClickType.SHIFT_RIGHT, color = ChatColor.RED, text = "delete button") { menu, player, template, slot ->
                template.boundButtons.remove(slot)
            }
        )
    }

    override fun getAbstractType(): Type {
        return TemplatePlaceholderButton::class.java
    }

    override fun clone(): MenuTemplateButton {
        return TemplatePlaceholderButton()
    }

    class Factory : MenuTemplateButtonFactory {
        override fun getName(): String {
            return "Template Placeholder"
        }

        override fun createButton(): MenuTemplateButton {
            return TemplatePlaceholderButton()
        }
    }

    companion object {
        // https://minecraft-heads.com/custom-heads/monsters/36801-magic-slime-green
        private const val TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhhNWE1ZDdmMTM1NTRiN2Q4OGQyNzY2ZTlhM2FmZTI3YTY4YTU2MmIxOWNmYzRkODQxYjAxMWZmZmQ3MTkzNiJ9fX0="
    }

}
