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
import co.aikar.commands.annotation.Optional
import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.scheme.SchemedMenuTests
import gg.scala.flavor.inject.Inject
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.config.CommonsGeneralConfig
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.menu.template.menu.TemplateEditorMenu
import net.evilblock.cubed.menu.template.menu.TemplatePreviewMenu
import net.evilblock.cubed.scoreboard.preview.ScoreboardPreviewHandler
import net.evilblock.cubed.scoreboard.preview.menu.ScoreboardTextEditor
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

@AutoRegister
@CommandPermission("op")
@CommandAlias("scala-commons|commons|sclib|lib")
object ScalaCommonsCommand : ScalaCommand()
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    val prefix by lazy {
        plugin.qolInternal.prefix
    }

    @Default
    @HelpCommand
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }

    @Subcommand("thread-dump")
    fun onThreadDump(sender: CommandSender)
    {
        sender.sendMessage("${CC.B_PRI}Threads:")

        Thread.getAllStackTraces().keys
            .sortedByDescending { it.name }
            .forEach {
                sender.sendMessage(" ${CC.WHITE}${
                    if (it.name.contains("ClusterId")) 
                        "mongodb-cluster-thread" else it.name
                }")
            }
    }

    @Subcommand("find-username")
    @Description("Searches the UUID cache for a username by the given uuid")
    fun onFindUsername(sender: CommandSender, @Name("uuid") uuid: UUID)
    {
        val username = ScalaStoreUuidCache.username(uuid)
        sender.sendMessage("$prefix ${CC.SEC}Found username: ${CC.PRI}$username")
    }

    @Subcommand("find-uuid")
    @Description("Searches the UUID cache for a UUID by the given username")
    @CommandCompletion("@players")
    fun onFindUUID(sender: CommandSender, @Name("username") username: String)
    {
        val uuid = ScalaStoreUuidCache.uniqueId(username)
        sender.sendMessage("$prefix ${CC.SEC}Found UUID: ${CC.PRI}$uuid")
    }

    @Subcommand("reload")
    fun onReload(sender: CommandSender)
    {
        plugin
            .configContainerized
            .loadAll()

        Reflection.DEBUG = plugin
            .config<CommonsGeneralConfig>()
            .debug

        sender.sendMessage("$prefix ${CC.GREEN}You've reloaded all commons' configs.")
    }

    @Subcommand("menu-template-editor")
    @Description("Opens the Menu Template Editor")
    fun onMenuTemplateEditor(player: Player)
    {
        TemplateEditorMenu().openMenu(player)
    }

    @Subcommand("menu-template-view")
    @Description("Opens the static view of a menu template")
    @CommandCompletion("@menutemplates")
    fun onMenuTemplateView(player: Player, @Name("template") template: MenuTemplate<*>)
    {
        TemplatePreviewMenu(template).openMenu(player)
    }

    @Subcommand("nametag-debug")
    @Description("View nametag debug")
    fun onNametagDebug(player: Player)
    {
        if (player.scoreboard == null)
        {
            throw ConditionFailedException("No scoreboard attached!")
        }

        for (team in player.scoreboard.teams)
        {
            val entries = team.entries
            player.sendMessage("${CC.GREEN}Team ${team.name} ${CC.GRAY}(${entries.size})")

            var last = true
            val print = entries.joinToString(transform = {
                last = !last
                if (last)
                {
                    "${CC.D_GRAY}$it"
                } else
                {
                    "${CC.GRAY}$it"
                }
            })

            player.sendMessage("${CC.D_AQUA}Entries: ${CC.GRAY}$print")
        }
    }

    @Subcommand("nametag-stats")
    @Description("View nametag update statistics")
    fun onNametagStats(sender: CommandSender)
    {
        var totalTeams = 0
        var totalEntries = 0

        val scoreboards = Bukkit.getOnlinePlayers().mapNotNull { it.scoreboard }
        for (scoreboard in scoreboards)
        {
            for (team in scoreboard.teams)
            {
                totalTeams++
                totalEntries += team.entries.size
            }
        }

        val builder = StringBuilder("$prefix ${CC.SEC}There ")
        builder.append(
            if (scoreboards.size == 1)
                "is ${CC.PRI}1 ${CC.SEC}scoreboard with "
            else
                "are ${CC.PRI}${scoreboards.size} ${CC.SEC}scoreboards with "
        )
        builder.append(
            if (totalTeams == 1)
                "${CC.PRI}1 ${CC.SEC}team and "
            else
                "${CC.PRI}${totalTeams} ${CC.SEC}teams and "
        )
        builder.append(
            if (totalEntries == 1)
                "is ${CC.PRI}1 ${CC.SEC}entry."
            else
                "are ${CC.PRI}${totalEntries} ${CC.SEC}entries."
        )

        sender.sendMessage(builder.toString())
    }

    @Subcommand("scoreboard-preview")
    @Description("Toggle preview mode")
    fun onScoreboardPreview(player: Player, @Name("global") @Optional global: Boolean?)
    {
        if (global != null && global)
        {
            ScoreboardPreviewHandler.previewGlobal = !ScoreboardPreviewHandler.previewGlobal
            return
        }

        if (ScoreboardPreviewHandler.previewMode.contains(player.uniqueId))
        {
            ScoreboardPreviewHandler.previewMode.remove(player.uniqueId)
        } else
        {
            ScoreboardPreviewHandler.previewMode.add(player.uniqueId)
        }

        player.sendMessage("$prefix ${CC.GREEN}You've toggled preview mode.")
    }

    @Subcommand("scoreboard-editor")
    @Description("Open the scoreboard preview editor")
    fun onScoreboardEditor(player: Player)
    {
        ScoreboardTextEditor().openMenu(player)
    }

    @Subcommand("glow|item-glow")
    fun onItemGlow(@Flags("itemheld") player: Player)
    {
        val itemInHand = player.inventory.itemInHand

        player.inventory.itemInHand = ItemBuilder.copyOf(itemInHand).glow().build()
        player.updateInventory()
    }
}
