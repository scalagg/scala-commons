package net.evilblock.cubed.command.serversync

import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.HelpCommand
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand

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
}
