/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed

import com.comphenix.protocol.ProtocolLibrary
import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.cache.uuid.impl.LocalUuidCacheTranslator
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.store.serializer.serializers.GsonSerializer
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
import net.evilblock.cubed.backup.BackupHandler
import net.evilblock.cubed.cache.BukkitUuidCacheListener
import net.evilblock.cubed.command.*
import net.evilblock.cubed.command.manager.CubedCommandManager
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.EntitySerializer
import net.evilblock.cubed.entity.animation.EntityAnimation
import net.evilblock.cubed.entity.hologram.command.HologramCommand
import net.evilblock.cubed.entity.hologram.personalized.PersonalizedHologramTask
import net.evilblock.cubed.entity.hologram.updating.UpdatingHologramTask
import net.evilblock.cubed.entity.listener.EntityWatcherListeners
import net.evilblock.cubed.entity.living.listener.LivingEntityListeners
import net.evilblock.cubed.entity.npc.command.NpcCommand
import net.evilblock.cubed.entity.npc.listener.NpcEntityTeamUpdatesListeners
import net.evilblock.cubed.entity.npc.strategy.NpcStrategyLogic
import net.evilblock.cubed.entity.packet.EntityPacketAdapter
import net.evilblock.cubed.entity.tool.EntityMoveTool
import net.evilblock.cubed.entity.tool.EntityPortTool
import net.evilblock.cubed.entity.tool.EntitySelectionTool
import net.evilblock.cubed.logging.LogHandler
import net.evilblock.cubed.logging.listener.ConsoleCommandLogListeners
import net.evilblock.cubed.menu.ButtonListeners
import net.evilblock.cubed.menu.MenuAutoUpdater
import net.evilblock.cubed.menu.menus.listener.SelectItemListeners
import net.evilblock.cubed.menu.protocol.InventoryPacketAdapter
import net.evilblock.cubed.menu.protocol.MenuPacketAdapter
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.menu.template.MenuTemplateButton
import net.evilblock.cubed.menu.template.MenuTemplateHandler
import net.evilblock.cubed.nametag.NametagHandler
import net.evilblock.cubed.nametag.NametagListener
import net.evilblock.cubed.nametag.update.NametagUpdatesListeners
import net.evilblock.cubed.poster.PosterHandler
import net.evilblock.cubed.poster.placed.PlacedPosterHandler
import net.evilblock.cubed.scheduler.CubedScheduler
import net.evilblock.cubed.scoreboard.ScoreboardListeners
import net.evilblock.cubed.scoreboard.ScoreboardThread
import net.evilblock.cubed.scoreboard.preview.ScoreboardPreviewHandler
import net.evilblock.cubed.serialize.BlockVectorAdapter
import net.evilblock.cubed.serialize.ItemStackAdapter
import net.evilblock.cubed.serialize.LocationAdapter
import net.evilblock.cubed.serialize.VectorAdapter
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializer
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.Rainbow
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.cubed.visibility.VisibilityListeners
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BlockVector
import org.spigotmc.SpigotConfig
import java.util.*

@Plugin(
    name = "Cubed",
    depends = [
        PluginDependency("helper"),
        PluginDependency("store-spigot"),
        PluginDependency("ProtocolLib")
    ]
)
class Cubed : ExtendedScalaPlugin()
{
    companion object
    {
        @JvmStatic
        lateinit var instance: Cubed

        @JvmStatic
        var startupFailed: Boolean = false

        @JvmStatic
        var running: Boolean = false
    }

    override fun enable()
    {
        instance = this

        Reflection.DEBUG = config.getBoolean("debug", false)

        try
        {
            saveDefaultConfig()
            runSetupCheck()

            CC.setup(
                CC.GREEN, CC.YELLOW
            )

            TimeZone.setDefault(
                TimeZone.getTimeZone(
                    config.getString("timezone", "EST")
                )
            )

            Serializers.useGsonBuilderThenRebuild { builder ->
                builder.registerTypeHierarchyAdapter(ItemStack::class.java, ItemStackAdapter())
                    .registerTypeHierarchyAdapter(Location::class.java, LocationAdapter())
                    .registerTypeHierarchyAdapter(Vector::class.java, VectorAdapter())
                    .registerTypeHierarchyAdapter(BlockVector::class.java, BlockVectorAdapter())
                    .registerTypeAdapter(Entity::class.java, EntitySerializer)
                    .registerTypeAdapter(EntityAnimation::class.java, AbstractTypeSerializer<EntityAnimation>())
                    .registerTypeAdapter(MenuTemplate::class.java, AbstractTypeSerializer<MenuTemplate<*>>())
                    .registerTypeAdapter(MenuTemplateButton::class.java, AbstractTypeSerializer<MenuTemplateButton>())
            }

            GsonSerializer.provideCustomGson {
                Serializers.gson
            }

            LogHandler.initialLoad()
            BackupHandler.initialLoad()

            EntityPacketAdapter

            loadCommands()
            loadHandlers()
            loadListeners()
            loadTasks()

            filterConsoleLogs()

            ScalaStoreUuidCache.configure(
                LocalUuidCacheTranslator
            )

            GlowEnchantment.registerEnchant()
        } catch (e: Exception)
        {
            e.printStackTrace()
            startupFailed = true
            server.shutdown()
            return
        }

        CubedScheduler.initialLoad()

        running = true
    }

    override fun disable()
    {
        try
        {
            saveHandlers()

            NpcStrategyLogic.restoreOriginalLines()

            if (isPostersEnabled())
            {
                PlacedPosterHandler.onDisable()
            }

            LogHandler.completeFlush()
        } catch (e: Exception)
        {
            e.printStackTrace()
        }

        running = false
    }

    /**
     * Runs important checks to ensure this server instance is properly setup.
     * If any check fails, the server automatically shuts down.
     *
     * @return true if checks are passed, otherwise false
     */
    private fun runSetupCheck(): Boolean
    {
        return if ((server.onlineMode && SpigotConfig.bungee) || (!server.onlineMode && !SpigotConfig.bungee))
        {
            logger.severe("*************************")
            logger.severe("ENVIRONMENT CHECK FAILED")
            logger.severe("Online Mode: ${server.onlineMode}")
            logger.severe("Bungee Mode: ${SpigotConfig.bungee}")
            logger.severe("*************************")

            server.shutdown()
            false
        } else
        {
            true
        }
    }

    private fun loadCommands()
    {
        val commandManager = CubedCommandManager(this)

        commandManager.registerCommand(CubedCommand)
        commandManager.registerCommand(CubedMenuCommand)

        commandManager.registerCommand(EntityCommand)
        commandManager.registerCommand(ItemHasKeyCommand)
        commandManager.registerCommand(PosterPlaceCommand)
        commandManager.registerCommand(SbReloadAllCommand)
        commandManager.registerCommand(SpawnArmorStandCommand)
        commandManager.registerCommand(HologramCommand)
        commandManager.registerCommand(NpcCommand)
    }

    private fun loadHandlers()
    {
        Tasks.delayed(2L) {
            EntityHandler.initialLoad()
        }

        if (isPostersEnabled())
        {
            PosterHandler.initialLoad()
            PlacedPosterHandler.initialLoad()
        }

        NametagHandler.initialLoad()
        MenuTemplateHandler.initialLoad()
        ScoreboardPreviewHandler.initialLoad()
    }

    fun saveHandlers()
    {
        EntityHandler.saveData()

        if (isPostersEnabled())
        {
            PosterHandler.saveData()
            PlacedPosterHandler.saveData()
        }

        MenuTemplateHandler.saveData()
    }

    private fun loadListeners()
    {
        listOf(
            InventoryPacketAdapter,
            MenuPacketAdapter,
        ).forEach {
            ProtocolLibrary.getProtocolManager()
                .addPacketListener(it)
        }

        listOf(
            ButtonListeners,
            SelectItemListeners,
            BukkitUuidCacheListener,
            EntityWatcherListeners,
            LivingEntityListeners,
            EntityMoveTool,
            EntityPortTool,
            EntitySelectionTool,
            NpcEntityTeamUpdatesListeners,
            MenuAutoUpdater,
            ConsoleCommandLogListeners,
            NametagUpdatesListeners,
            VisibilityListeners,
            ScoreboardListeners,
            NametagListener,
        ).forEach {
            server.pluginManager.registerEvents(it, this)
        }
    }

    private fun loadTasks()
    {
        ScoreboardThread().start()

        Tasks.asyncTimer(NpcStrategyLogic, 20L, 20L)
        Tasks.asyncTimer(MenuAutoUpdater, 2L, 2L)
        Tasks.asyncTimer(Rainbow, 4L, 4L)
        Tasks.asyncTimer(UpdatingHologramTask, 10L, 10L)
        Tasks.asyncTimer(PersonalizedHologramTask, 10L, 10L)
    }

    /**
     * Hi-jack Bukkit's logging filter to suppress the server's complaints about commands being dispatched async
     */
    private fun filterConsoleLogs()
    {
        Bukkit.getLogger().setFilter {
            return@setFilter !it.message.contains("Command Dispatched Async")
                    && !it.message.contains("Please notify author of plugin")
        }
    }

    fun isPostersEnabled(): Boolean
    {
        return config.getBoolean("posters.enabled", false)
    }

}
