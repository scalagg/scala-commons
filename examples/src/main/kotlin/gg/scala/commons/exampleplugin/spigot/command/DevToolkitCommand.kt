package gg.scala.commons.exampleplugin.spigot.command

import gg.scala.commons.acf.CommandHelp
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.Default
import gg.scala.commons.acf.annotation.HelpCommand
import gg.scala.commons.acf.annotation.Subcommand
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.distribution.MenuRowDistribution
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.minecraft.server.v1_8_R3.Material
import org.bukkit.entity.Player

@AutoRegister
@CommandAlias("toolkit")
object DevToolkitCommand : ScalaCommand()
{
    @Default
    @HelpCommand
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }

    @Subcommand("demo1-dynamic")
    fun onDemo1Dynamic(player: ScalaPlayer)
    {
        object : PaginatedMenu()
        {
            init
            {
                this.autoUpdate = true
                this.distribution = MenuRowDistribution.SQUASHED
            }

            override fun size(buttons: Map<Int, Button>) = 18
            override fun getMaxItemsPerPage(player: Player) = 9

            override fun getAllPagesButtons(player: Player): Map<Int, Button>
            {
                val map = mutableMapOf<Int, Button>()
                for (i in 1..((2..5).random()))
                {
                    map[map.size] = ItemBuilder
                        .of(org.bukkit.Material.CAKE)
                        .name("${CC.GREEN}#$i")
                        .toButton()
                }
                return map
            }

            override fun getPrePaginatedTitle(player: Player) = "Demo"
        }.openMenu(player.bukkit())
    }

    @Subcommand("demo1")
    fun onDemo1(player: ScalaPlayer)
    {
        var iterations = 0

        object : PaginatedMenu()
        {
            override fun getAllPagesButtons(player: Player): Map<Int, Button>
            {
                println(++iterations)
                return mutableMapOf()
            }

            override fun getPrePaginatedTitle(player: Player) = "Demo"
        }.openMenu(player.bukkit())
    }

    @Subcommand("demo2")
    fun onDemo2(player: ScalaPlayer)
    {
        var iterations = 0

        object : Menu("Demo")
        {
            override fun getButtons(player: Player): Map<Int, Button>
            {
                println(++iterations)
                return mutableMapOf()
            }
        }.openMenu(player.bukkit())
    }
}
