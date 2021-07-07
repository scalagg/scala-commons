/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.template.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.menu.template.menu.TemplateEditorMenu
import org.bukkit.entity.Player

object MenuTemplateEditorCommand {

    @Command(
        names = ["cubed menu-template editor"],
        description = "Opens the Menu Template Editor",
        permission = "op"
    )
    @JvmStatic
    fun execute(player: Player) {
        TemplateEditorMenu().openMenu(player)
    }

}