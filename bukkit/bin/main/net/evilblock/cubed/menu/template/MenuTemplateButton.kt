/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.template

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

abstract class MenuTemplateButton : AbstractTypeSerializable {

    open fun getIcon(): ItemStack {
        return ItemStack(Material.AIR)
    }

    open fun getEditorIcon(): ItemStack {
        return getIcon()
    }

    open fun getActions(): List<ButtonAction> {
        return emptyList()
    }

    open fun getEditorActions(): List<ButtonAction> {
        return emptyList()
    }

    inner class ButtonAction(
        val clickType: ClickType,
        val color: ChatColor,
        val text: String = "",
        val onClick: (Menu, Player, MenuTemplate<*>, Int) -> Unit
    )

    abstract inner class MenuButton(private val menu: Menu, private val template: MenuTemplate<*>) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            val item = getButtonIcon().clone()

            if (getButtonActions().firstOrNull { it.text.isNotEmpty() } == null) {
                return item
            }

            val itemBuilder = ItemBuilder.copyOf(item).addToLore("")

            for (action in getButtonActions()) {
                if (action.text.isEmpty()) {
                    continue
                }

                val shift = if (action.clickType.isShiftClick) {
                    "Shift "
                } else {
                    ""
                }

                val clickName = when {
                    action.clickType.isLeftClick -> "Left-Click"
                    action.clickType.isRightClick -> "Right-Click"
                    action.clickType == ClickType.MIDDLE -> "Middle-Click"
                    else -> "Not_Bound-Click"
                }

                itemBuilder.addToLore("${action.color}$shift$clickName ${action.color}to ${action.text}")
            }

            return itemBuilder.build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            for (action in getButtonActions()) {
                if (action.clickType == clickType) {
                    action.onClick.invoke(menu, player, template, slot)
                }
            }
        }

        abstract fun getButtonIcon(): ItemStack

        abstract fun getButtonActions(): List<MenuTemplateButton.ButtonAction>
    }

    inner class ViewButton(menu: Menu, template: MenuTemplate<*>) : MenuButton(menu = menu, template = template) {
        override fun getButtonIcon(): ItemStack {
            return getIcon()
        }

        override fun getButtonActions(): List<MenuTemplateButton.ButtonAction> {
            return getActions()
        }
    }

    inner class EditorButton(menu: Menu, template: MenuTemplate<*>) : MenuButton(menu = menu, template = template) {
        override fun getButtonIcon(): ItemStack {
            return getEditorIcon()
        }

        override fun getButtonActions(): List<MenuTemplateButton.ButtonAction> {
            return getEditorActions()
        }
    }

}