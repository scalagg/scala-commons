package net.evilblock.cubed.entity.hologram.menu.button

import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.cubed.entity.item.FloatingItem
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.menus.SelectItemStackMenu
import net.evilblock.cubed.util.text.TextSplitter
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class UpdateFloatingItemButton(private val hologram: HologramEntity) : Button() {

    override fun getName(player: Player): String {
        return "${ChatColor.YELLOW}${ChatColor.BOLD}Update Floating Item"
    }

    override fun getDescription(player: Player): List<String> {
        return arrayListOf<String>().also { desc ->
            desc.addAll(TextSplitter.split(text = "Update the floating item that appears above the hologram."))
            desc.add("")
            desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to update item")

            if (hologram.floatingItem != null) {
                desc.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.GREEN}to remove item")
            }
        }
    }

    override fun getMaterial(player: Player): Material {
        return Material.ITEM_FRAME
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (clickType.isLeftClick) {
            SelectItemStackMenu { itemStack ->
                hologram.setFloatingItem(FloatingItem(itemStack, hologram.getFloatingItemLocation()))
            }
        } else if (clickType.isRightClick && hologram.floatingItem != null) {
            hologram.removeFloatingItem()
        }
    }

}