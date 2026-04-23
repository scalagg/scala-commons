/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.npc.menu

import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.menu.EditEntityMenu
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.text.TextUtil
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class EditNpcTextMenu(private val npc: NpcEntity) : TextEditorMenu(lines = npc.getLines()) {

    init {
        supportsColors = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit NPC Text"
    }

    override fun onClose(player: Player) {
        Tasks.delayed(1L) {
            EditEntityMenu(npc).openMenu(player)
        }
    }

    override fun onSave(player: Player, list: List<String>) {
        val lines = list.map { line ->
            if (TextUtil.isBlank(text = line)) {
                ""
            } else {
                line
            }
        }

        npc.updateLines(lines)

        Tasks.async {
            EntityHandler.close()
        }
    }

    override fun getPromptBuilder(player: Player, index: Int): EzPrompt.Builder {
        return EzPrompt.Builder()
            .promptText("${ChatColor.GREEN}Please enter the new text.")
            .charLimit(100)
    }

}
