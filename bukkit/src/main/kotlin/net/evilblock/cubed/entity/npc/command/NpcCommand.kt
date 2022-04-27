/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.npc.command

import co.aikar.commands.CommandHelp
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.*
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.menu.EditEntityMenu
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.entity.tool.EntitySelectionTool
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.math.Numbers
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.lang.Math.floor

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
    fun onCreate(player: Player, @Name("text") @Single text: String)
    {
        if (text.length > 48)
        {
            throw ConditionFailedException("Text cannot be longer than 48 characters.");
        }

        val loc = player.location
        loc.x = floor(loc.x) + 0.5
        loc.z = floor(loc.z) + 0.5

        val npc = NpcEntity(listOf(ChatColor.translateAlternateColorCodes('&', text)), loc)
        npc.initializeData()
        npc.spawn(player)

        EntityHandler.trackEntity(npc)
        EntityHandler.close()

        player.sendMessage("${ChatColor.GREEN}Successfully created the NPC!")
    }

    @Subcommand("delete-near")
    @Description("Deletes NPCs within a radius of blocks")
    @CommandPermission("commons.npcs.delete")
    fun onDeleteNear(player: Player, @Name("radius") @Default("5") radius: Int)
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

    @Subcommand("spawn-defined")
    @Description("Spawns an NPC defined in the Cubed config")
    @CommandPermission("commons.npcs.create")
    fun execute(player: Player, @Name("id") id: String)
    {
        val config = Cubed.instance.config
        if (!config.contains("defined-npcs.$id"))
        {
            player.sendMessage("${ChatColor.RED}That NPC hasn't been defined!")
            return
        }

        val loc = player.location
        loc.x = kotlin.math.floor(loc.x) + 0.5
        loc.z = kotlin.math.floor(loc.z) + 0.5

        val npc = NpcEntity(
            config.getStringList("defined-npcs.$id.lines").map { ChatColor.translateAlternateColorCodes('&', it) }, loc
        )
        npc.initializeData()

        if (config.contains("defined-npcs.$id.texture-value") && config.contains("defined-npcs.$id.texture-signature"))
        {
            npc.updateTexture(
                config.getString("defined-npcs.$id.texture-value"),
                config.getString("defined-npcs.$id.texture-signature")
            )
        } else if (config.contains("defined-npcs.$id.texture"))
        {
            npc.updateTextureByUsername(config.getString("defined-npcs.$id.texture")) { success, exception ->
                if (!success)
                {
                    player.sendMessage("${ChatColor.RED}Failed to fetch NPC's texture!")
                }
            }
        }

        if (config.contains("defined-npcs.$id.command"))
        {
            npc.command = config.getString("defined-npcs.$id.command")
        }

        npc.spawn(player)

        EntityHandler.trackEntity(npc)
        EntityHandler.close()

        player.sendMessage("${ChatColor.GREEN}Successfully spawned the NPC!")
    }
}
