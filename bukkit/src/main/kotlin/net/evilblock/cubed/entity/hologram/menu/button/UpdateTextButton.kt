package net.evilblock.cubed.entity.hologram.menu.button

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.cubed.entity.hologram.menu.EditHologramTextMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.text.TextSplitter
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class UpdateTextButton(private val hologram: HologramEntity) : Button() {

    override fun getName(player: Player): String {
        return "${CC.GREEN}Update Text"
    }

    override fun getDescription(player: Player): List<String> {
        return arrayListOf<String>().also { desc ->
            desc.addAll(TextSplitter.split(text = "Update the text that this hologram displays."))
            desc.add("")
            desc.add("${CC.GREEN}Left-Click to update text.")
        }
    }

    override fun getMaterial(player: Player): XMaterial
    {
        return XMaterial
            .matchXMaterial(
                Material.SIGN
            )
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        EditHologramTextMenu(hologram).openMenu(player)
    }

}
