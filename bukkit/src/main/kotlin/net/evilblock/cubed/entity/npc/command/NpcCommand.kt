

package net.evilblock.cubed.entity.npc.command

import co.aikar.commands.CommandHelp
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.*
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.command.EntityCommandHelper
import net.evilblock.cubed.entity.menu.EditEntityMenu
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.entity.tool.EntitySelectionTool
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Color
import net.evilblock.cubed.util.bukkit.FancyMessage
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.math.Numbers
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.ChatColor
import org.bukkit.entity.Player

@AutoRegister
@CommandAlias("npc")
@CommandPermission("commons.npcs")
object NpcCommand : ScalaCommand()
{
    @Default
    @HelpCommand
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }

    @Subcommand("create")
    @Description("Spawn an NPC")
    @CommandPermission("commons.npcs.create")
    fun onCreate(player: Player, @Single text: String)
    {
        if (text.length > 48)
        {
            throw ConditionFailedException("Text cannot be longer than 48 characters.");
        }

        val loc = player.location
        loc.x = kotlin.math.floor(loc.x) + 0.5
        loc.z = kotlin.math.floor(loc.z) + 0.5

        val npc = NpcEntity(
            listOf(Color.translate(text)), loc
        )

        npc.initializeData()
        npc.spawn(player)

        EntityHandler.trackEntity(npc)
        EntityHandler.close()

        player.sendMessage("${ChatColor.GREEN}Successfully created the NPC!")
    }

    @Subcommand("delete-near")
    @Description("Deletes NPCs within a radius of blocks")
    @CommandPermission("commons.npcs.delete")
    fun onDeleteNear(player: Player, @Default("5") radius: Int)
    {
        val inRadius = arrayListOf<NpcEntity>()

        for (npc in EntityHandler.getEntities().filterIsInstance<NpcEntity>())
        {
            if (player.world == npc.location.world && player.location.distanceSquared(npc.location) <= radius)
            {
                inRadius.add(npc)
            }
        }

        if (inRadius.isEmpty())
        {
            player.sendMessage("${ChatColor.RED}There aren't any NPCs nearby!")
            return
        }

        ConfirmMenu("Delete ${Numbers.format(inRadius.size)} NPCs?") { confirmed ->
            if (confirmed)
            {
                Tasks.async {
                    for (npc in inRadius)
                    {
                        EntityHandler.forgetEntity(npc)
                        npc.destroyForCurrentWatchers()
                    }
                }
                player.sendMessage("${ChatColor.GREEN}Successfully deleted ${Numbers.format(inRadius.size)} NPCs!")
            }
        }.openMenu(player)
    }

    @Subcommand("teleport")
    @CommandPermission("commons.npcs.teleport")
    @Description("Teleport to an NPC.")
    fun onTeleport(player: Player, npc: NpcEntity)
    {
        EntityCommandHelper.teleportToAnEntity(player, npc)
    }

    @Subcommand("list")
    @CommandPermission("commons.npcs.list")
    @Description("View all NPC entities.")
    fun onEntityList(player: Player)
    {
        EntityCommandHelper.listEntities<NpcEntity>(player)
    }

    @Subcommand("manage")
    @Description("Manage an NPC")
    @CommandPermission("commons.npcs.manage")
    fun onManage(player: Player)
    {
        player.sendMessage("${ChatColor.GREEN}Select (right-click) an NPC to manage...")

        EntitySelectionTool.attachSelectionHandler(player) { entity ->
            if (entity is NpcEntity)
            {
                EditEntityMenu(entity).openMenu(player)
            }
        }
    }
}
