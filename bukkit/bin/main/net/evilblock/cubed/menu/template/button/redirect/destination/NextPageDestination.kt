/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.template.button.redirect.destination

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.menu.template.button.redirect.MenuDestination
import org.bukkit.entity.Player

class NextPageDestination : MenuDestination {

    override fun getName(): String {
        return "Next Page"
    }

    override fun openMenu(player: Player, menu: Menu, template: MenuTemplate<*>) {
        if (menu is PaginatedMenu) {
            if (hasNext(player, menu)) {
                menu.modPage(player, 1)
                Button.playNeutral(player)
            }
        }
    }

    private fun hasNext(player: Player, menu: PaginatedMenu): Boolean {
        val pg = menu.page + 1
        return pg > 0 && menu.getPages(player) >= pg
    }

}