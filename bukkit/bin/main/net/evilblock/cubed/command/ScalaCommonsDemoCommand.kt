package net.evilblock.cubed.command

import co.aikar.commands.CommandHelp
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.*
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.scheme.SchemedMenuTests
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.menus.RangeEditorMenu
import net.evilblock.cubed.menu.menus.SelectColorMenu
import net.evilblock.cubed.menu.menus.SelectItemStackMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.render.GraphicalTable
import net.evilblock.cubed.util.nms.MinecraftReflection
import net.evilblock.cubed.util.text.ProgressBarBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
@AutoRegister
@CommandAlias("scala-commons-demo")
@CommandPermission("commons.command.demo")
object ScalaCommonsDemoCommand : ScalaCommand()
{
    @Default
    @HelpCommand
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }

    @Subcommand("test-scheme-menu-single")
    fun onTestScheme(player: Player)
    {
        SchemedMenuTests.test(player)
    }

    @Subcommand("progress-bar")
    @Description("A demo of Cubed's progress bar utility.")
    fun onProgressBar(sender: Player, progress: Double)
    {
        if (progress > 100.0)
        {
            throw ConditionFailedException("This value must be less than 100.0.")
        }

        ProgressBarBuilder(
            char = '❘',
            steps = 100
        ).build(progress).apply {
            sender.sendMessage(this)
        }
    }

    @Subcommand("graphical-tables")
    @Description("A demo of Cubed's graphical tables.")
    fun onGraphicalTables(sender: Player)
    {
        val table = GraphicalTable()
        table.title("Hey ${sender.name}!")
        table.borders(true)

        for (row in 0..2)
        {
            for (column in 0..9)
            {
                table.addEntry(row, column, "$row, $column")
            }
        }

        table.render().forEach {
            sender.sendMessage(it)
        }
    }

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
        kotlin.runCatching {
            Reflection
                .getDeclaredMethod(
                    MinecraftReflection.getCraftBukkitClass("entity.CraftHumanEntity")!!,
                    "openCustomInventory",
                    Inventory::class.java,
                    MinecraftReflection.getNMSClass(
                        "${
                            if (MinecraftReflection.isModern())
                                "level." else ""
                        }EntityPlayer"
                    )!!,
                    if (MinecraftReflection.isModern())
                    {
                        Reflection.getClassSuppressed(
                            "net.minecraft.world.inventory.Containers"
                        )!!
                    } else
                    {
                        String::class.java
                    }
                )
        }.onFailure {
            Logger.getGlobal().log(
                Level.SEVERE, "Failed to instantiate inventory logic", it
            )
        }

        kotlin.runCatching {
            SelectColorMenu {
                player.sendMessage("${CC.SEC}You selected: ${CC.PRI}$it")
            }.openMenu(player)
        }.onFailure {
            Logger.getGlobal().log(
                Level.SEVERE, "Failed to open color menu", it
            )
        }
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
