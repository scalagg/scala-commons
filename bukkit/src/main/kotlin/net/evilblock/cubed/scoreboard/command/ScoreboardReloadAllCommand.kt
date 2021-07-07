package net.evilblock.cubed.scoreboard.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.scoreboard.Scoreboard
import net.evilblock.cubed.scoreboard.ScoreboardHandler
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

object ScoreboardReloadAllCommand {

    @Command(
        names = ["sb-reload-all"],
        description = "Reloads the scoreboard for all players",
        permission = "op"
    )
    @JvmStatic
    fun execute(sender: CommandSender) {
        for (player in Bukkit.getOnlinePlayers()) {
            player.scoreboard = null
            ScoreboardHandler.scoreboards[player.uniqueId] = Scoreboard(player)
        }
    }

}