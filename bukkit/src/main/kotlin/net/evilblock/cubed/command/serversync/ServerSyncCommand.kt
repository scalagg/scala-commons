package net.evilblock.cubed.command.serversync

import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.HelpCommand
import co.aikar.commands.annotation.Subcommand
import gg.scala.commons.agnostic.sync.server.ServerContainer
import gg.scala.commons.agnostic.sync.server.impl.GameServer
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import net.evilblock.cubed.util.CC

/**
 * @author GrowlyX
 * @since 8/30/2022
 */
@AutoRegister
@CommandAlias("serversync")
@CommandPermission("commons.command.serversync")
object ServerSyncCommand : ScalaCommand()
{
    @Default
    @HelpCommand
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }

    @Subcommand("view")
    fun onView(player: ScalaPlayer)
    {
        player.sendMessage("${CC.GREEN}[ServerSync servers:]")

        ServerContainer
            .allServers<GameServer>()
            .sortedBy { it.id }
            .forEach {
                player.sendMessage(" ${CC.GRAY}- ${CC.WHITE}${it.id} ${CC.GRAY}(${it.getPlayersCount()}/${it.getMaxPlayers()})")
            }
    }
}
