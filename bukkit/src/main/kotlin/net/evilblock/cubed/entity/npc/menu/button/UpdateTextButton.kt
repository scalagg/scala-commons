package net.evilblock.cubed.entity.npc.menu.button

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.entity.npc.menu.EditNpcTextMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class UpdateTextButton(private val npc: NpcEntity) : Button() {

    override fun getName(player: Player): String {
        return "${CC.GREEN}Update Text"
    }

    override fun getDescription(player: Player): List<String> {
        return listOf(
            "${CC.GRAY}Update the lines of text that",
            "${CC.GRAY}appear above this NPC.",
            "",
            "${CC.GREEN}Left-Click to update text."
        )
    }

    override fun getMaterial(player: Player): XMaterial
    {
        return XMaterial.OAK_WALL_SIGN
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        EditNpcTextMenu(npc).openMenu(player)
    }

}
