package net.evilblock.cubed.entity.npc.menu.button

import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.entity.npc.menu.EditNpcMessagesMenu
import net.evilblock.cubed.menu.Button
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class UpdateMessagesButton(private val npc: NpcEntity) : Button() {

    override fun getName(player: Player): String {
        return "${ChatColor.YELLOW}${ChatColor.BOLD}Update Messages"
    }

    override fun getDescription(player: Player): List<String> {
        return listOf(
            "${ChatColor.GRAY}Update the messages that are sent to",
            "${ChatColor.GRAY}a player when they right-click this NPC.",
            "",
            "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to update messages",
            "${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to unset messages"
        )
    }

    override fun getMaterial(player: Player): Material {
        return Material.BOOK_AND_QUILL
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (clickType.isLeftClick) {
            if (npc.messages == null) {
                npc.messages = arrayListOf()
            }

            EditNpcMessagesMenu(npc).openMenu(player)
        } else {
            if (npc.messages != null) {
                npc.messages = null
                player.sendMessage("${ChatColor.GREEN}Unset messages!")
            }
        }
    }

}