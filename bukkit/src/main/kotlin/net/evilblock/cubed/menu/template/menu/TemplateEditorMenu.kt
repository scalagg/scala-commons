/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.template.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.menu.template.MenuTemplateFactory
import net.evilblock.cubed.menu.template.MenuTemplateHandler
import net.evilblock.cubed.util.CC
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class TemplateEditorMenu : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Menu Template Editor"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()

        buttons[2] = CreateTemplateButton()

        for (i in 9..17) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 0, " ")
        }

        return buttons
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (menuTemplate in MenuTemplateHandler.getTemplates()) {
            buttons[buttons.size] = TemplateButton(menuTemplate)
        }

        return buttons
    }

    override fun getButtonsStartOffset(): Int {
        return 9
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 36
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    private inner class CreateTemplateButton : AddButton() {
        override fun getName(player: Player): String {
            return "${CC.GRAY}» ${CC.PRI}Create New Template"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "${CC.GRAY}Create a new template by completing",
                "${CC.GRAY}the setup procedure.",
                "",
                "${CC.GREEN}Left-Click to create a new template."
            )
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                PickFactoryMenu { factory ->
                    EzPrompt.Builder()
                        .promptText(EzPrompt.IDENTIFIER_PROMPT)
                        .charLimit(16)
                        .regex(EzPrompt.IDENTIFIER_REGEX)
                        .acceptInput { input ->
                            if (MenuTemplateHandler.findTemplate(input) != null) {
                                player.sendMessage("${CC.RED}A template's ID must be unique, and a template with the ID `$input` already exists.")
                                return@acceptInput
                            }

                            factory.startProcedure(input) { template ->
                                MenuTemplateHandler.trackTemplate(template)
                                MenuTemplateHandler.saveData()

                                player.sendMessage("${CC.GREEN}Successfully created a new template.")

                                openMenu(player)
                            }
                        }
                        .build()
                        .start(player)
                }.openMenu(player)
            }
        }
    }

    private inner class TemplateButton(private val template: MenuTemplate<*>) : Button() {
        override fun getName(player: Player): String {
            return "${CC.YELLOW}${template.getName()}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("${CC.GRAY}Edit the layout of the template by")
            description.add("${CC.GRAY}painting buttons into the menu, and")
            description.add("${CC.GRAY}then binding actions to the buttons.")
            description.add("")
            description.add("${CC.GREEN}Left-Click to edit template.")
            description.add("${CC.RED}Right-Click to delete template.")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.ITEM_FRAME
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditTemplateLayoutMenu(template).openMenu(player)
            }

            if (clickType.isRightClick) {
                ConfirmMenu("Are you sure?") { confirmed ->
                    if (confirmed) {
                        MenuTemplateHandler.forgetTemplate(template)
                        MenuTemplateHandler.saveData()

                        TemplateEditorMenu().openMenu(player)
                    }
                }.openMenu(player)
            }
        }
    }

    private inner class PickFactoryMenu(private val onSelect: (MenuTemplateFactory) -> Unit) : Menu() {
        override fun getTitle(player: Player): String {
            return "Pick Template Type"
        }

        override fun getButtons(player: Player): Map<Int, Button> {
            val buttons = hashMapOf<Int, Button>()

            for (factory in MenuTemplateHandler.getFactories()) {
                buttons[buttons.size] = FactoryButton(factory)
            }

            return buttons
        }

        private inner class FactoryButton(private val factory: MenuTemplateFactory) : Button() {
            override fun getName(player: Player): String {
                return "${CC.GRAY}» ${CC.PRI}${factory.getName()}"
            }

            override fun getMaterial(player: Player): Material {
                return Material.ITEM_FRAME
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
                if (clickType.isLeftClick) {
                    onSelect.invoke(factory)
                }
            }
        }
    }

}