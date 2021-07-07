/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.menu

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import org.bukkit.Material
import org.bukkit.entity.Player

class EditEntityMenu(private val entity: Entity) : Menu() {

    override fun getTitle(player: Player): String {
        return "Edit ${entity.getTypeName()} #${entity.id}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        val editorButtons = entity.getEditorButtons(player, this)
        if (editorButtons.size in 3..5) {
            val startAt: Int = when (editorButtons.size) {
                3 -> 2
                4 -> 1
                5 -> 0
                else -> 0
            }

            for ((index, button) in editorButtons.withIndex()) {
                buttons[startAt + (index * 2)] = button
            }
        } else {
            for (button in editorButtons) {
                buttons[buttons.size] = button
            }
        }

        for (i in 0..8) {
            if (!buttons.containsKey(i)) {
                buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 7, " ")
            }
        }

        return buttons
    }

}