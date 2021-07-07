/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.template

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.template.button.redirect.MenuDestination
import net.evilblock.cubed.menu.template.menu.TemplateEditorMenu
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class MenuTemplate<T>(val id: String) : AbstractTypeSerializable {

    private var name: String = id

    val boundButtons: MutableMap<Int, MenuTemplateButton> = hashMapOf()

    open fun getName(): String {
        return name
    }

    open fun getTemplateButtonFactories(): List<MenuTemplateButtonFactory> {
        return emptyList()
    }

    open fun getTemplateButtonRedirectionDestinations(): List<MenuDestination> {
        return emptyList()
    }

    abstract fun getListEntries(): List<T>

    abstract fun createEntryButton(entry: T) : Button

    open fun onClose(player: Player) {

    }

    open fun onEditorClose(player: Player) {
        Tasks.delayed(1L) {
            TemplateEditorMenu().openMenu(player)
        }
    }

    class TemplateParameterType : ParameterType<MenuTemplate<*>> {
        override fun transform(sender: CommandSender, source: String): MenuTemplate<*>? {
            return MenuTemplateHandler.findTemplate(source)
        }

        override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
            val completions = arrayListOf<String>()
            for (template in MenuTemplateHandler.getTemplates()) {
                if (template.id.startsWith(source, ignoreCase = true)) {
                    completions.add(template.id)
                }
            }
            return completions
        }
    }

}