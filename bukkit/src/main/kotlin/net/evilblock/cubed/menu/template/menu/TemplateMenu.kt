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
import net.evilblock.cubed.menu.template.button.TemplatePlaceholderButton
import org.bukkit.entity.Player

class TemplateMenu<T>(
    private val template: MenuTemplate<T>,
    autoUpdate: Boolean = false,
    autoUpdateInterval: Long = 500L
) : Menu() {

    init {
        this.updateAfterClick = true
        this.autoUpdate = autoUpdate

        if (this.autoUpdate) {
            this.autoUpdateInterval = autoUpdateInterval
        }
    }

    override fun getTitle(player: Player): String {
        return template.getName()
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for ((slot, button) in template.boundButtons) {
            if (button is TemplatePlaceholderButton) {
                continue
            }

            buttons[slot] = button.ViewButton(this, template)
        }

        val placeholderSlots = template.boundButtons.filter { it.value is TemplatePlaceholderButton }.keys.sorted()
        val listEntries = template.getListEntries()

        listEntries.forEachIndexed { index, entry ->
            if (index < placeholderSlots.size) {
                buttons[placeholderSlots[index]] = template.createEntryButton(entry)
            }
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        val maxSlot = template.boundButtons.maxBy { it.key }?.key ?: return 9

        var rows = maxSlot / 9
        if (maxSlot / 9 > 0) {
            rows++
        }

        return rows * 9
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            template.onClose(player)
        }
    }

}