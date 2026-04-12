

package net.evilblock.cubed.menu.template.button.redirect

import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.template.MenuTemplate
import org.bukkit.entity.Player

interface MenuDestination {

    fun getName(): String

    fun openMenu(player: Player, menu: Menu, template: MenuTemplate<*>)

}