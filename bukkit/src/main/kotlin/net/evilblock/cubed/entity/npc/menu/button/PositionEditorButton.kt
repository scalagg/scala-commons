package net.evilblock.cubed.entity.npc.menu.button

import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.entity.npc.menu.EditNpcPositionMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.text.TextSplitter
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class PositionEditorButton(private val npc: NpcEntity) : Button() {

    override fun getName(player: Player): String {
        return "${CC.GRAY}» ${CC.PRI}Position Editor"
    }

    override fun getDescription(player: Player): List<String> {
        return arrayListOf<String>().also { desc ->
            desc.addAll(TextSplitter.split(text = "Update this NPC's position using fine adjustments."))
            desc.add("")
            desc.add("${CC.GREEN}Left-Click to open position editor.")
        }
    }

    override fun getMaterial(player: Player): Material {
        return Material.COMPASS
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (clickType.isLeftClick) {
            EditNpcPositionMenu(npc).openMenu(player)
        }
    }

}