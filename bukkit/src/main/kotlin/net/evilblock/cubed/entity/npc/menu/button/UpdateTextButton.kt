package net.evilblock.cubed.entity.npc.menu.button

import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.entity.npc.menu.EditNpcTextMenu
import net.evilblock.cubed.menu.Button
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class UpdateTextButton(private val npc: NpcEntity) : Button() {

    override fun getName(player: Player): String {
        return "${ChatColor.YELLOW}${ChatColor.BOLD}Update Text"
    }

    override fun getDescription(player: Player): List<String> {
        return listOf(
            "",
            "${ChatColor.GRAY}Update the lines of text that",
            "${ChatColor.GRAY}appear above this NPC.",
            "",
            "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to update text"
        )
    }

    override fun getMaterial(player: Player): Material {
        return Material.SIGN
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        EditNpcTextMenu(npc).openMenu(player)
    }

}