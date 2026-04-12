

package net.evilblock.cubed.menu.template.menu

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.menu.template.MenuTemplateButton
import net.evilblock.cubed.menu.template.MenuTemplateButtonFactory
import net.evilblock.cubed.menu.template.MenuTemplateHandler
import net.evilblock.cubed.menu.template.button.BasicTemplateButton
import net.evilblock.cubed.menu.template.button.TemplatePlaceholderButton
import net.evilblock.cubed.util.CC
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class EditTemplateLayoutMenu(private val template: MenuTemplate<*>) : Menu() {

    companion object {
        private val PLACEMENT_CURSOR = ItemStack(Material.CHEST)
    }

    private var selectedButton: MenuTemplateButton? = null

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Editing Template - ${template.getName()}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for ((slot, button) in template.boundButtons) {
            buttons[slot] = button.EditorButton(this, template)
        }

        val size = size(buttons)
        for (i in 0 until size) {
            if (!buttons.containsKey(i)) {
                buttons[i] = EmptySlotButton()
            }
        }

        return buttons
    }

    override fun acceptsInsertedItem(player: Player, itemStack: ItemStack, slot: Int): Boolean {
        if (template.boundButtons.containsKey(slot)) {
            return false
        }

        if (player.openInventory.cursor == PLACEMENT_CURSOR && selectedButton != null) {
            template.boundButtons[slot] = selectedButton!!
            player.openInventory.cursor = null
            player.updateInventory()
            return true
        }

        template.boundButtons[slot] = BasicTemplateButton(itemStack.clone())
        MenuTemplateHandler.close()

        return true
    }

    override fun acceptsDraggedItems(player: Player, items: Map<Int, ItemStack>): Boolean {
        for (inserted in items) {
            if (template.boundButtons.containsKey(inserted.key)) {
                return false
            }
        }

        for (inserted in items) {
            template.boundButtons[inserted.key] = BasicTemplateButton(inserted.value)
        }

        return true
    }

    override fun onOpen(player: Player) {
        if (selectedButton != null) {
            player.openInventory.cursor = PLACEMENT_CURSOR
            player.updateInventory()
        }
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            template.onEditorClose(player)
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    private inner class EmptySlotButton : Button() {
        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isShiftClick && clickType.isLeftClick) {
                if (!template.boundButtons.containsKey(slot)) {

                    
                    val templateButtonTypes = template.getTemplateButtonFactories()
                    if (templateButtonTypes.isEmpty()) {
                        template.boundButtons[slot] = TemplatePlaceholderButton()
                        MenuTemplateHandler.close()
                    } else {
                        PickFactoryButton(template) { factory ->
                            this@EditTemplateLayoutMenu.selectedButton = factory.createButton()
                            this@EditTemplateLayoutMenu.openMenu(player)
                        }
                    }
                }
            }
        }
    }

    private inner class PickFactoryButton(private val template: MenuTemplate<*>, private val onSelect: (MenuTemplateButtonFactory) -> Unit) : Menu() {
        override fun getTitle(player: Player): String {
            return "Pick Button Type"
        }

        override fun getButtons(player: Player): Map<Int, Button> {
            val buttons = hashMapOf<Int, Button>()

            buttons[0] = FactoryButton(TemplatePlaceholderButton.Factory())

            for (factory in template.getTemplateButtonFactories()) {
                buttons[buttons.size] = FactoryButton(factory)
            }

            return buttons
        }

        private inner class FactoryButton(private val factory: MenuTemplateButtonFactory) : Button() {
            override fun getName(player: Player): String {
                return "${CC.GREEN}${factory.getName()}"
            }

            override fun getMaterial(player: Player): XMaterial
            {
                return XMaterial.STONE_BUTTON
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
                if (clickType.isLeftClick) {
                    onSelect.invoke(factory)
                }
            }
        }
    }

}
