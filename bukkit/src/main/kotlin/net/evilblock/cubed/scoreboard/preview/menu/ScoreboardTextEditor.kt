package net.evilblock.cubed.scoreboard.preview.menu

import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.scoreboard.preview.ScoreboardPreviewHandler
import net.evilblock.cubed.util.text.TextUtil
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class ScoreboardTextEditor : TextEditorMenu(lines = ScoreboardPreviewHandler.lines) {

    init {
        supportsColors = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit Scoreboard Text"
    }

    override fun onClose(player: Player) {
        Tasks.async {
            ScoreboardPreviewHandler.saveData()
        }
    }

    override fun onSave(player: Player, list: List<String>) {
        ScoreboardPreviewHandler.lines = list.map { line ->
            if (TextUtil.isBlank(text = line)) {
                ""
            } else {
                line
            }
        }.toMutableList()

        Tasks.async {
            ScoreboardPreviewHandler.saveData()
        }
    }

    override fun getPromptBuilder(player: Player, index: Int): EzPrompt.Builder {
        return EzPrompt.Builder()
            .promptText("${ChatColor.GREEN}Please enter the new text for scoreboard line $index.")
            .charLimit(100)
    }

}