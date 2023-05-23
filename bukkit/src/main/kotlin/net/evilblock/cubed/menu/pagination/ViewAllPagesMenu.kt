/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.pagination

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.entity.Player

class ViewAllPagesMenu(
    private val menu: PaginatedMenu,
    private val pages: Int
) : Menu() {

    init {
        autoUpdate = true
        placeholdBorders = true
    }

    override fun getTitle(player: Player): String {
        return "Page Selector"
    }

    override fun size(buttons: Map<Int, Button>): Int
    {
        return super.size(buttons) + 9
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = HashMap<Int, Button>()

        var index = 10
        for (i in 1..pages) {
            buttons[index++] = JumpToPageButton(i, menu, menu.page == i)
            if ((index - 8) % 9 == 0) {
                index += 2
            }
        }

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean)
    {
        Tasks.delayed(1L)
        {
            menu.openMenu(player)
        }
    }

}
