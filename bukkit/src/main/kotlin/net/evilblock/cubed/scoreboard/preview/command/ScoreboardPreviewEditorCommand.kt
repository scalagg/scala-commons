package net.evilblock.cubed.scoreboard.preview.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.scoreboard.preview.menu.ScoreboardTextEditor
import org.bukkit.entity.Player

object ScoreboardPreviewEditorCommand {

    @Command(
        names = ["cubed scoreboard editor"],
        description = "Open the scoreboard preview editor",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        ScoreboardTextEditor().openMenu(player)
    }

}