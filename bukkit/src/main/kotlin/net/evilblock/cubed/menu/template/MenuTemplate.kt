

package net.evilblock.cubed.menu.template

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.template.button.redirect.MenuDestination
import net.evilblock.cubed.menu.template.menu.TemplateEditorMenu
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable
import net.evilblock.cubed.util.bukkit.Tasks
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
}