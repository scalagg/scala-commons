

package net.evilblock.cubed.menu.template.button

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.cubed.menu.template.MenuTemplateButton
import net.evilblock.cubed.menu.template.MenuTemplateHandler
import net.evilblock.cubed.menu.template.button.redirect.MenuDestination
import net.evilblock.cubed.util.CC
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type

class BasicTemplateButton(private var icon: ItemStack) : MenuTemplateButton(), CloneableMenuTemplateButton {

    private var commands: MutableList<String> = arrayListOf()
    private var redirect: MenuDestination? = null

    fun getCommands(): List<String> {
        return commands.toList()
    }

    override fun getIcon(): ItemStack {
        if (icon.type == XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial() && (!icon.hasItemMeta() || !icon.itemMeta.hasDisplayName())) {
            val meta = icon.itemMeta
            meta.displayName = " "
            icon.itemMeta = meta
        }

        return icon
    }

    override fun getActions(): List<ButtonAction> {
        if (redirect != null) {
            return listOf(
                ButtonAction(clickType = ClickType.LEFT, color = ChatColor.GREEN) { menu, player, menuTemplate, slot ->
                    redirect!!.openMenu(player, menu, menuTemplate)
                }
            )
        }

        return listOf(
            ButtonAction(clickType = ClickType.LEFT, color = ChatColor.GREEN) { menu, player, menuTemplate, slot ->
                for (command in commands) {
                    player.performCommand(command)
                }
            }
        )
    }

    override fun getEditorActions(): List<ButtonAction> {
        return listOf(
            ButtonAction(clickType = ClickType.SHIFT_LEFT, color = ChatColor.GREEN, text = "edit actions") { menu, player, template, slot ->
                ActionEditor(menu, template).openMenu(player)
            },
            ButtonAction(clickType = ClickType.SHIFT_RIGHT, color = ChatColor.RED, text = "delete button") { menu, player, template, slot ->
                template.boundButtons.remove(slot)
            },
            ButtonAction(clickType = ClickType.MIDDLE, color = ChatColor.AQUA, text = "copy item") { menu, player, template, slot ->
                player.openInventory.cursor = icon.clone()

                player.updateInventory()
            }
        )
    }

    override fun getAbstractType(): Type {
        return BasicTemplateButton::class.java
    }

    override fun clone(): MenuTemplateButton {
        val clone = BasicTemplateButton(icon.clone())
        clone.commands = commands.toMutableList()
        clone.redirect = redirect
        return clone
    }

    private inner class ActionEditor(private val previousMenu: Menu, private val template: MenuTemplate<*>): Menu() {
        override fun getTitle(player: Player): String {
            return "Edit Button Actions"
        }

        override fun getButtons(player: Player): Map<Int, Button> {
            val buttons = hashMapOf<Int, Button>()

            buttons[0] = SetRedirectButton()
            buttons[1] = EditCommandsButton()

            return buttons
        }

        override fun onClose(player: Player, manualClose: Boolean) {
            if (manualClose) {
                Tasks.delayed(1L) {
                    previousMenu.openMenu(player)
                }
            }
        }

        private inner class SetRedirectButton : Button() {
            override fun getName(player: Player): String {
                return "${CC.PRI}Set Redirect"
            }

            override fun getMaterial(player: Player): XMaterial {
                return XMaterial.ENDER_PEARL
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
                PickRedirectDestination { redirect = it }.openMenu(player)
            }

            private inner class PickRedirectDestination(private val onSelect: (MenuDestination) -> Unit) : Menu() {
                override fun getTitle(player: Player): String {
                    return "Pick Redirect Destination"
                }

                override fun getButtons(player: Player): Map<Int, Button> {
                    val buttons = hashMapOf<Int, Button>()

                    for (destination in template.getTemplateButtonRedirectionDestinations()) {
                        buttons[buttons.size] = DestinationButton(destination)
                    }

                    return buttons
                }

                private inner class DestinationButton(private val destination: MenuDestination) : Button() {
                    override fun getName(player: Player): String {
                        return "${ChatColor.YELLOW}${destination.getName()}"
                    }

                    override fun getMaterial(player: Player): XMaterial {
                        return XMaterial.BEACON
                    }

                    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
                        if (clickType.isLeftClick) {
                            onSelect.invoke(destination)
                            this@ActionEditor.openMenu(player)
                        }
                    }
                }
            }
        }

        private inner class EditCommandsButton : Button() {
            override fun getName(player: Player): String {
                return "${CC.PRI}Edit Commands"
            }

            override fun getMaterial(player: Player): XMaterial {
                return XMaterial.COMMAND_BLOCK
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
                CommandsTextEditor(previousMenu).openMenu(player)
            }
        }
    }

    private inner class CommandsTextEditor(private val previousMenu: Menu) : TextEditorMenu(lines = commands.toList()) {
        override fun getPrePaginatedTitle(player: Player): String {
            return "Edit Button Commands"
        }

        override fun onSave(player: Player, list: List<String>) {
            commands = list.toMutableList()
            MenuTemplateHandler.close()
        }

        override fun onClose(player: Player) {
            Tasks.delayed(1L) {
                previousMenu.openMenu(player)
            }
        }

        override fun getPromptBuilder(player: Player, index: Int): EzPrompt.Builder {
            return EzPrompt.Builder()
                .promptText("${ChatColor.GREEN}Please enter the new command.")
                .charLimit(100)
        }
    }

}
