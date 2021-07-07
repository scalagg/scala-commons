/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.template.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.menu.template.menu.TemplatePreviewMenu
import org.bukkit.entity.Player

object MenuTemplateViewCommand {

    @Command(
        names = ["cubed menu-template view"],
        description = "Opens the static view of a menu template",
        permission = "op"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "template") template: MenuTemplate<*>) {
        TemplatePreviewMenu(template).openMenu(player)
    }

}