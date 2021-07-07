package net.evilblock.cubed.entity.hologram.menu.button

import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.cubed.entity.hologram.menu.EditHologramTextMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.text.TextSplitter
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class UpdateTextButton(private val hologram: HologramEntity) : Button() {

    override fun getName(player: Player): String {
        return "${ChatColor.YELLOW}${ChatColor.BOLD}Update Text"
    }

    override fun getDescription(player: Player): List<String> {
        return arrayListOf<String>().also { desc ->
            desc.addAll(TextSplitter.split(text = "Update the text that this hologram displays."))
            desc.add("")
            desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to update text")
        }
    }

    override fun getMaterial(player: Player): Material {
        return Material.SIGN
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        EditHologramTextMenu(hologram).openMenu(player)
    }

}