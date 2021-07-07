package net.evilblock.cubed.entity.npc.menu

import net.evilblock.cubed.entity.menu.EditEntityMenu
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class EditNpcMessagesMenu(private val npc: NpcEntity) : TextEditorMenu(lines = npc.messages ?: arrayListOf()) {

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit Messages"
    }

    override fun onSave(player: Player, list: List<String>) {
        npc.messages = list.map { ChatColor.translateAlternateColorCodes('&', it) }.toMutableList()
    }

    override fun onClose(player: Player) {
        Tasks.delayed(1L) {
            EditEntityMenu(npc).openMenu(player)
        }
    }

}