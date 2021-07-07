package net.evilblock.cubed.scoreboard.preview.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.flag.Flag
import net.evilblock.cubed.scoreboard.preview.ScoreboardPreviewHandler
import org.bukkit.entity.Player

object ScoreboardPreviewCommand {

    @Command(
        names = ["cubed scoreboard preview"],
        description = "Toggle preview mode",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Flag(value = ["g", "global"], defaultValue = false) global: Boolean) {
        if (global) {
            ScoreboardPreviewHandler.previewGlobal = !ScoreboardPreviewHandler.previewGlobal
            return
        }

        if (ScoreboardPreviewHandler.previewMode.contains(player.uniqueId)) {
            ScoreboardPreviewHandler.previewMode.remove(player.uniqueId)
        } else {
            ScoreboardPreviewHandler.previewMode.add(player.uniqueId)
        }
    }

}