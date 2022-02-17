package net.evilblock.cubed.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.menus.RangeEditorMenu
import net.evilblock.cubed.menu.menus.SelectColorMenu
import net.evilblock.cubed.menu.menus.SelectItemStackMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
@CommandAlias("cubed-menu")
@CommandPermission("cubed.command.menu-demo")
object CubedMenuCommand : BaseCommand()
{
    @Subcommand("paginated")
    @Description("A demo of Cubed's paginated menus.")
    fun onPaginatedDemo(player: Player)
    {
        PaginatedMenuDemo().openMenu(player)
    }

    @Subcommand("confirmation")
    @Description("A demo of Cubed's confirmation menu.")
    fun onConfirmationMenu(player: Player)
    {
        ConfirmMenu(
            title = "Are you sure?",
            confirm = true
        ) {
            player.sendMessage("${CC.SEC}You selected: ${CC.PRI}$it")
        }.openMenu(player)
    }

    @Subcommand("color")
    @Description("A demo of Cubed's color selection menu.")
    fun onColorSelectionMenu(player: Player)
    {
        SelectColorMenu {
            player.sendMessage("${CC.SEC}You selected: ${CC.PRI}$it")
        }.openMenu(player)
    }

    @Subcommand("item-stack")
    @Description("A demo of Cubed's item-stack selection menu.")
    fun onItemStackMenu(player: Player)
    {
        SelectItemStackMenu {
            player.sendMessage("${CC.SEC}Your input: ${CC.PRI}${it.type.name}")
        }.openMenu(player)
    }

    @Subcommand("range")
    @Description("A demo of Cubed's range selection menu.")
    fun onRangeEditorMenu(player: Player)
    {
        RangeEditorMenuDemo(player).openMenu(player)
    }

    class RangeEditorMenuDemo(
        private val player: Player
    ) : RangeEditorMenu(1, 100)
    {
        override fun onChange(min: Int, max: Int)
        {
            player.sendMessage("${CC.SEC}The min is: ${CC.PRI}$min${CC.SEC}, and the max is: ${CC.PRI}$max${CC.SEC}.")
        }
    }

    class PaginatedMenuDemo : PaginatedMenu()
    {
        override fun getPrePaginatedTitle(player: Player) = "Paginated Menu"

        override fun getAllPagesButtons(player: Player): Map<Int, Button>
        {
            val button = ItemBuilder(Material.PAPER).toButton()

            return mutableMapOf<Int, Button>().also {
                for (i in 0..400)
                {
                    it[it.size] = button
                }
            }
        }
    }
}
