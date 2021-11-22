/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.pagination

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.BackButton
import org.bukkit.entity.Player

class ViewAllPagesMenu(private val menu: PaginatedMenu) : Menu() {

    init {
        autoUpdate = true
    }

    override fun getTitle(player: Player): String {
        return "Jump to page"
    }

    override fun size(buttons: Map<Int, Button>): Int = 36

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = HashMap<Int, Button>()

        buttons[0] = BackButton {
            menu.openMenu(player)
        }

        var index = 10
        for (i in 1..menu.getPages(player)) {
            buttons[index++] = JumpToPageButton(i, menu, menu.page == i)
            if ((index - 8) % 9 == 0) {
                index += 2
            }
        }

        return buttons
    }

}
