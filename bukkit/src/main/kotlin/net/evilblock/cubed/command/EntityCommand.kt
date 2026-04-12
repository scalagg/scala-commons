

package net.evilblock.cubed.command

import co.aikar.commands.CommandHelp
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.HelpCommand
import co.aikar.commands.annotation.Name
import co.aikar.commands.annotation.Subcommand
import com.google.gson.reflect.TypeToken
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.commons.util.Files
import gg.scala.flavor.inject.Inject
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.menu.EditEntityMenu
import net.evilblock.cubed.entity.thread.EntityTickThread
import net.evilblock.cubed.entity.tool.EntityPortTool
import net.evilblock.cubed.entity.tool.EntitySelectionTool
import net.evilblock.cubed.serializers.Serializers
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
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    @Default
    @HelpCommand
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }

    @Subcommand("debug")
    fun onDebug(player: ScalaPlayer)
    {
        EntityTickThread.DEBUG = !EntityTickThread.DEBUG
        player.sendMessage("${CC.SEC}Debug enabled: ${CC.PRI}${EntityTickThread.DEBUG}")
    }

    @Subcommand("debug-view")
    fun onDebugView(player: ScalaPlayer)
    {
        if (EntityHandler.toggleDebugView(player.bukkit()))
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
    fun onExport(player: ScalaPlayer)
    {
        if (EntityPortTool.isTracking(player.bukkit()))
        {
            EntityPortTool.stopTracking(player.bukkit())
            player.sendMessage("${CC.RED}Stopped exporting entities.")
        } else
        {
            EntityPortTool.startTracking(player.bukkit())
            player.sendMessage("${CC.GREEN}Starting export selection... Right-click any hologram/NPC to mark for export!")
            player.sendMessage("${CC.GREEN}Shift left-click while sneaking to finish export!")
        }
    }

    @Subcommand("fast-edit|fe")
    @Description("Toggles fast edit for entities")
    @CommandPermission("commons.entities.manage")
    fun onFastEdit(player: ScalaPlayer)
    {
        if (player.bukkit().hasMetadata("EntityFastEdit"))
        {
            player.bukkit().removeMetadata("EntityFastEdit", plugin)
            player.sendMessage("${CC.RED}Fast edit is now disabled!")
        } else
        {
            player.bukkit().setMetadata("EntityFastEdit", FixedMetadataValue(plugin, true))
            player.sendMessage("${CC.GREEN}Fast edit is now enabled!")
        }
    }

    @Subcommand("fix-null-worlds")
    @Description("Deletes entities with null worlds (debugging)")
    @CommandPermission("op")
    fun onFixNullWorlds(player: ScalaPlayer)
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
    fun onImport(player: ScalaPlayer, fileName: String)
    {
        val file = EntityPortTool.findImportDataFile(fileName)

        if (!file.exists())
        {
            throw ConditionFailedException("Couldn't find file: $fileName")
        }

        Files.read(file) { reader ->
            val entities = Serializers.gson.fromJson(reader, object : TypeToken<Set<Entity>>() {}.type) as Set<Entity>

            for (entity in entities)
            {
                entity.updateLocation(player.bukkit().location)
                EntityHandler.trackEntity(entity)
            }

            EntityHandler.close()

            player.sendMessage("${CC.GREEN}Successfully imported ${entities.size} from ${file.path}!")
        }
    }

    @Subcommand("manage")
    @Description("Opens the untyped entity manager menu")
    @CommandPermission("commons.entities.manage")
    fun onManage(player: ScalaPlayer)
    {
        player.sendMessage("${CC.GREEN}Select (right-click) an NPC to manage...")

        EntitySelectionTool.attachSelectionHandler(player.bukkit()) { entity ->
            EditEntityMenu(entity).openMenu(player.bukkit())
        }
    }

    @Subcommand("nearby")
    @Description("Lists nearby entities")
    @CommandPermission("op")
    fun onNearby(player: ScalaPlayer, @Default("64.0") radius: Double)
    {
        val nearby = EntityHandler.getEntities()
            .filter {
                it.location.world == player.bukkit().location.world &&
                        it.location.distanceSquared(player.bukkit().location) < radius
            }

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
    fun onStats(player: ScalaPlayer)
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
    fun onTeleport(player: ScalaPlayer, entity: Entity)
    {
        player.teleport(entity.location)
    }
}
