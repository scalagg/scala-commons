/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.hologram.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.*
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.cubed.entity.hologram.updating.FormatUpdatingHologramEntity
import net.evilblock.cubed.entity.menu.EditEntityMenu
import net.evilblock.cubed.entity.tool.EntitySelectionTool
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.text.TextUtil
import org.bukkit.ChatColor
import org.bukkit.entity.Player

@CommandAlias("hologram|holo")
@CommandPermission("cubed.holograms")
object HologramCommand : BaseCommand() {

    @Default
    @HelpCommand
    fun onHelp(help: CommandHelp) {
        help.showHelp()
    }

    @Subcommand("create")
    @Syntax("<text> [-u]")
    @Description("Create a hologram")
    @CommandPermission("cubed.holograms.create")
    fun onCreate(player: Player, input: String) {
        val updating = input.endsWith("-s")
        val text = input.replaceFirst("-s", "")
        val hologramText = if (TextUtil.isBlank(text = text)) "" else text

        val hologram = if (updating) {
            FormatUpdatingHologramEntity(text = ChatColor.translateAlternateColorCodes('&', hologramText), location = player.eyeLocation)
        } else {
            HologramEntity(text = ChatColor.translateAlternateColorCodes('&', hologramText), location = player.eyeLocation)
        }

        hologram.initializeData()

        EntityHandler.trackEntity(hologram)
        EntityHandler.saveData()

        player.sendMessage("${ChatColor.GREEN}Successfully created hologram!")
    }

    @Subcommand("manage")
    @Description("Manage a hologram")
    @CommandPermission("cubed.holograms.manage")
    fun onManage(player: Player) {
        player.sendMessage("${ChatColor.GREEN}Select (right-click) a hologram to manage...")

        EntitySelectionTool.attachSelectionHandler(player) { entity ->
            if (entity is HologramEntity) {
                Tasks.sync {
                    EditEntityMenu(entity).openMenu(player)
                }
            }
        }
    }

}