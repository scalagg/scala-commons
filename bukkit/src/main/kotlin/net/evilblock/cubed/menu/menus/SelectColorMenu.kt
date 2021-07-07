package net.evilblock.cubed.menu.menus

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.ColorUtil
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class SelectColorMenu(private val use: (Color) -> Unit) : Menu() {

    override fun getTitle(player: Player): String {
        return "Select Color"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (color in ColorUtil.getPredefinedColors()) {
                buttons[buttons.size] = ColorButton(color)
            }
        }
    }

    private inner class ColorButton(private val color: Color) : Button() {
        override fun getName(player: Player): String {
            return ColorUtil.toChatColor(color).toString() + ColorUtil.getColorName(color)
        }

        override fun getMaterial(player: Player): Material {
            return Material.WOOL
        }

        override fun getDamageValue(player: Player): Byte {
            return ColorUtil.toWoolData(ColorUtil.toChatColor(color)).toByte()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                player.closeInventory()
                use.invoke(color)
            }
        }
    }

}