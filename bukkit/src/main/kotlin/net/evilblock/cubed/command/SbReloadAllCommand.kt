

package net.evilblock.cubed.command

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.flavor.inject.Inject
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.scoreboard.Scoreboard
import net.evilblock.cubed.scoreboard.ScoreboardHandler
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@AutoRegister
object SbReloadAllCommand : ScalaCommand()
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    @CommandAlias("sb-reload-all")
    @Description("Reloads the scoreboard for all players")
    @CommandPermission("op")
    fun execute(sender: CommandSender)
    {
        for (player in Bukkit.getOnlinePlayers())
        {
            player.scoreboard = null

            ScoreboardHandler.scoreboards[player.uniqueId] =
                Scoreboard(player, this.plugin)
        }
    }
}
