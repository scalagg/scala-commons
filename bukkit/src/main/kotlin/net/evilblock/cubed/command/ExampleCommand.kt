package net.evilblock.cubed.command

import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.HelpCommand
import co.aikar.commands.annotation.Subcommand
import gg.scala.commons.annotations.commands.AssignPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer

/**
 * @author GrowlyX
 * @since 10/10/2022
 */
@AutoRegister
@CommandAlias("test")
@CommandPermission("sclib.command.example")
object ExampleCommand : ScalaCommand()
{
    @Default
    @HelpCommand
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }

    @Subcommand("noperm")
    fun onNoPerm(player: ScalaPlayer)
    {
        player.sendMessage("heyy")
    }

    @AssignPermission
    @Subcommand("one")
    fun onOne(player: ScalaPlayer)
    {
        player.sendMessage("hi")
    }

    @AssignPermission
    @Subcommand("two|three")
    fun on2Or3(player: ScalaPlayer)
    {
        player.sendMessage("hi 2, 3")
    }
}
