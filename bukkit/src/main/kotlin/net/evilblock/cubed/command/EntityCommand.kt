/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command

import co.aikar.commands.CommandHelp
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.*
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.menu.EditEntityMenu
import net.evilblock.cubed.entity.thread.EntityTickThread
import net.evilblock.cubed.entity.tool.EntityPortTool
import net.evilblock.cubed.entity.tool.EntitySelectionTool
import net.evilblock.cubed.util.CC
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import java.text.NumberFormat

@AutoRegister
@CommandAlias("entity")
@CommandPermission("commons.entities")
object EntityCommand : ScalaCommand()
{
    @Default
    @HelpCommand
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }

    @Subcommand("debug")
    fun onDebug(player: Player)
    {
        EntityTickThread.DEBUG = !EntityTickThread.DEBUG
        player.sendMessage("${CC.SEC}Debug enabled: ${CC.PRI}${EntityTickThread.DEBUG.toString()}")
    }

    @Subcommand("debug-view")
    fun onDebugView(player: Player)
    {
        if (EntityHandler.toggleDebugView(player))
        {
            player.sendMessage("${CC.GREEN}Debug view enabled!")
        } else
        {
            player.sendMessage("${CC.RED}Debug view disabled!")
        }
    }

    @Subcommand("export")
    @Description("Export a selection of entities")
    @CommandPermission("commons.entities.export")
    fun onExport(player: Player)
    {
        if (EntityPortTool.isTracking(player))
        {
            EntityPortTool.stopTracking(player)
            player.sendMessage("${CC.RED}Stopped exporting entities.")
        } else
        {
            EntityPortTool.startTracking(player)
            player.sendMessage("${CC.GREEN}Starting export selection... Right-click any hologram/NPC to mark for export!")
            player.sendMessage("${CC.GREEN}Shift left-click while sneaking to finish export!")
        }
    }

    @Subcommand("fast-edit|fe")
    @Description("Toggles fast edit for entities")
    @CommandPermission("commons.entities.manage")
    fun onFastEdit(player: Player)
    {
        if (player.hasMetadata("EntityFastEdit"))
        {
            player.removeMetadata("EntityFastEdit", Cubed.instance)
            player.sendMessage("${CC.RED}Fast edit is now disabled!")
        } else
        {
            player.setMetadata("EntityFastEdit", FixedMetadataValue(Cubed.instance, true))
            player.sendMessage("${CC.GREEN}Fast edit is now enabled!")
        }
    }

    @Subcommand("fix-null-worlds")
    @Description("Deletes entities with null worlds (debugging)")
    @CommandPermission("op")
    fun onFixNullWorlds(player: Player)
    {
        var deleted = 0
        for (entity in EntityHandler.getEntities())
        {
            if (entity.location.world == null)
            {
                deleted++
                EntityHandler.forgetEntity(entity)
                entity.destroyForCurrentWatchers()
            }
        }
        player.sendMessage("${CC.GREEN}Fixed $deleted entities with null worlds!")
    }

    @Subcommand("import")
    @Description("Import entities from a file")
    @CommandPermission("commons.entities.import")
    fun onImport(player: Player, @Name("fileName") fileName: String)
    {
        val file = EntityPortTool.findImportDataFile(fileName)
        if (!file.exists())
        {
            throw ConditionFailedException("Couldn't find file: $fileName")
        }

        val entities = EntityPortTool.import(file)
        for (entity in entities)
        {
            entity.updateLocation(player.location)
            EntityHandler.trackEntity(entity)
        }

        EntityHandler.close()

        player.sendMessage("${CC.GREEN}Successfully imported ${entities.size} from ${file.path}!")
    }

    @Subcommand("manage")
    @Description("Opens the untyped entity manager menu")
    @CommandPermission("commons.entities.manage")
    fun onManage(player: Player)
    {
        player.sendMessage("${CC.GREEN}Select (right-click) an NPC to manage...")

        EntitySelectionTool.attachSelectionHandler(player) { entity ->
            EditEntityMenu(entity).openMenu(player)
        }
    }

    @Subcommand("nearby")
    @Description("Lists nearby entities")
    @CommandPermission("op")
    fun onNearby(player: Player, @Name("radius") @Default("64.0") radius: Double)
    {
        val nearby = EntityHandler.getEntities()
            .filter { it.location.world == player.location.world && it.location.distanceSquared(player.location) < radius }
        if (nearby.isEmpty())
        {
            player.sendMessage("${CC.RED}There are no nearby entities!")
            return
        }

        for (entity in nearby)
        {
            player.sendMessage("${entity.id} - ${entity.uuid}")
        }
    }

    @Subcommand("save")
    @Description("Saves all entities to the disk")
    @CommandPermission("commons.entities.save")
    fun onSave(sender: CommandSender)
    {
        EntityHandler.close()
        sender.sendMessage("${CC.B_GREEN}Saved entities!")
    }

    @Subcommand("stats")
    @Description("Shows entity stats")
    @CommandPermission("commons.entities.stats")
    fun onStats(player: Player)
    {
        player.sendMessage("")
        player.sendMessage("${ChatColor.GREEN}Printing stats for entities...")
        player.sendMessage("")
        player.sendMessage("${ChatColor.GOLD}Current ID: ${CC.WHITE}${Entity.currId()}")
        player.sendMessage(
            "${ChatColor.GOLD}Tracked entities: ${CC.WHITE}${
                NumberFormat.getInstance().format(EntityHandler.getEntities().size)
            }"
        )
        player.sendMessage("")
    }

    @Subcommand("tp|teleport")
    @Description("Teleport to a Hologram by supplying the Hologram's ID")
    @CommandPermission("commons.entities.manage")
    fun onTeleport(player: Player, @Name("id") entity: Entity)
    {
        player.teleport(entity.location)
    }
}
