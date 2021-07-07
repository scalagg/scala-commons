package net.evilblock.cubed.entity.npc.menu.button

import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.entity.npc.menu.EditNpcGearMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.text.TextSplitter
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class UpdateGearButton(private val npc: NpcEntity) : Button() {

    override fun getName(player: Player): String {
        return "${ChatColor.YELLOW}${ChatColor.BOLD}Update Gear"
    }

    override fun getDescription(player: Player): List<String> {
        return arrayListOf<String>().also { desc ->
            desc.addAll(TextSplitter.split(text = "Update this NPC's weapon and armor."))
            desc.add("")
            desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to update gear")
        }
    }

    override fun getMaterial(player: Player): Material {
        return Material.DIAMOND_HELMET
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        EditNpcGearMenu(npc).openMenu(player)
    }

}