/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed

import com.comphenix.protocol.ProtocolLibrary
import net.evilblock.cubed.backup.BackupHandler
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.EntitySerializer
import net.evilblock.cubed.entity.animation.EntityAnimation
import net.evilblock.cubed.entity.hologram.personalized.PersonalizedHologramTask
import net.evilblock.cubed.entity.hologram.updating.UpdatingHologramTask
import net.evilblock.cubed.entity.listener.EntityWatcherListeners
import net.evilblock.cubed.entity.living.listener.LivingEntityListeners
import net.evilblock.cubed.entity.npc.listener.NpcEntityTeamUpdatesListeners
import net.evilblock.cubed.entity.npc.strategy.NpcStrategyLogic
import net.evilblock.cubed.entity.packet.EntityPacketAdapter
import net.evilblock.cubed.entity.tool.EntityMoveTool
import net.evilblock.cubed.entity.tool.EntityPortTool
import net.evilblock.cubed.entity.tool.EntitySelectionTool
import net.evilblock.cubed.logging.ErrorHandler
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
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.poster.PosterHandler
import net.evilblock.cubed.poster.placed.PlacedPosterHandler
import net.evilblock.cubed.scoreboard.ScoreboardListeners
import net.evilblock.cubed.scoreboard.ScoreboardThread
import net.evilblock.cubed.scoreboard.preview.ScoreboardPreviewHandler
import net.evilblock.cubed.serialize.BlockVectorAdapter
import net.evilblock.cubed.serialize.ItemStackAdapter
import net.evilblock.cubed.serialize.LocationAdapter
import net.evilblock.cubed.serialize.VectorAdapter
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializer
import net.evilblock.cubed.store.mongo.Mongo
import net.evilblock.cubed.store.redis.Redis
import net.evilblock.cubed.store.uuidcache.UUIDCache
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.Rainbow
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.cubed.visibility.VisibilityListeners
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.BlockVector
import org.spigotmc.SpigotConfig
import java.util.*

/**
 * Cubed Engine - Rapid plugin development
 */
class Cubed : JavaPlugin() {

    companion object {
        @JvmStatic lateinit var instance: Cubed
        @JvmStatic var startupFailed: Boolean = false
        @JvmStatic var running: Boolean = false
    }

    lateinit var uuidCache: UUIDCache

    val redis: Redis = Redis()
    val mongo: Mongo = Mongo()

    override fun onEnable() {
        instance = this

        Reflection.DEBUG = config.getBoolean("debug", false)

        try {
            saveDefaultConfig()
            runSetupCheck()

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

            LogHandler.initialLoad()
            BackupHandler.initialLoad()

            loadStore()
            loadCommands()
            loadHandlers()
            loadListeners()
            loadTasks()

            filterConsoleLogs()

            GlowEnchantment.registerEnchant()

//        ProtocolLibrary.getProtocolManager().addPacketListener(DebugPacketAdapter)
        } catch (e: Exception) {
            e.printStackTrace()
            startupFailed = true
            server.shutdown()
            return
        }

        running = true
    }

    override fun onDisable() {
        try {
            saveHandlers()

            NpcStrategyLogic.restoreOriginalLines()

            if (isPostersEnabled()) {
                PlacedPosterHandler.onDisable()
            }

            LogHandler.onDisable()
        } catch (e: Exception) {
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
    private fun runSetupCheck(): Boolean {
        return if ((server.onlineMode && SpigotConfig.bungee) || (!server.onlineMode && !SpigotConfig.bungee)) {
            logger.severe("*************************")
            logger.severe("ENVIRONMENT CHECK FAILED")
            logger.severe("Online Mode: ${server.onlineMode}")
            logger.severe("Bungee Mode: ${SpigotConfig.bungee}")
            logger.severe("*************************")

            server.shutdown()
            false
        } else {
            true
        }
    }

    private fun loadStore() {
//        try {
//            redis.connect(CubedConfig.getRedisURI())
//        } catch (e: Exception) {
//            throw RuntimeException("Couldn't connect to redis server at ${CubedConfig.getRedisURI()}", e)
//        }
//
//        logger.info("Connected to redis server")

        try {
            mongo.connect(CubedConfig.getMongoURI())
        } catch (e: Exception) {
            throw RuntimeException("Couldn't connect to mongodb server at ${CubedConfig.getMongoURI()}", e)
        }

        logger.info("Connected to mongodb server")

//        try {
//            uuidCache = RedisUUIDCache(redis)
//            uuidCache.load()
//        } catch (e: Exception) {
//            throw RuntimeException("Failed to load UUID cache", e)
//        }
    }

    private fun loadCommands() {
//        CommandHandler.load()
//
//        CommandHandler.registerClass(CubedCommands.javaClass)
//
//        CommandHandler.registerClass(FindUsernameCommand.javaClass)
//        CommandHandler.registerClass(FindUUIDCommand.javaClass)
//
//        CommandHandler.registerClass(EntityFixNullWorldsCommand.javaClass)
//        CommandHandler.registerClass(EntityImportCommand.javaClass)
//        CommandHandler.registerClass(EntityExportCommand.javaClass)
//        CommandHandler.registerClass(EntityFastEditCommand.javaClass)
//        CommandHandler.registerClass(EntityManageCommand.javaClass)
//        CommandHandler.registerClass(EntityNearbyCommand.javaClass)
//        CommandHandler.registerClass(EntitySaveCommand.javaClass)
//        CommandHandler.registerClass(EntityStatsCommand.javaClass)
//        CommandHandler.registerClass(EntityTeleportCommand.javaClass)
//
//        CommandHandler.registerClass(NpcCreateCommand.javaClass)
//        CommandHandler.registerClass(NpcSpawnCommand.javaClass)
//        CommandHandler.registerClass(NpcDeleteNearCommand.javaClass)
//        CommandHandler.registerClass(NpcManageCommand.javaClass)
//
//        CommandHandler.registerClass(HologramCreateCommand.javaClass)
//        CommandHandler.registerClass(HologramManageCommand.javaClass)
//
//        CommandHandler.registerClass(NametagStatsCommand.javaClass)
//        CommandHandler.registerClass(NametagDebugCommand.javaClass)
//
//        CommandHandler.registerClass(MenuTemplateEditorCommand.javaClass)
//        CommandHandler.registerClass(MenuTemplateViewCommand.javaClass)
//
//        CommandHandler.registerClass(ScoreboardPreviewCommand.javaClass)
//        CommandHandler.registerClass(ScoreboardPreviewEditorCommand.javaClass)
//        CommandHandler.registerClass(ScoreboardReloadAllCommand.javaClass)
//
//        CommandHandler.registerClass(PosterPlaceCommand.javaClass)
//
//        CommandHandler.registerParameterType(Entity::class.java, EntityParameterType())
//        CommandHandler.registerParameterType(NpcEntity::class.java, NpcEntityParameterType())
//        CommandHandler.registerParameterType(NpcTexture::class.java, NpcTexture.NpcTextureParameterType)
//        CommandHandler.registerParameterType(MenuTemplate::class.java, MenuTemplate.TemplateParameterType())
//
//        logger.info("Loaded command handler")
    }

    private fun loadHandlers() {
        Tasks.delayed(2L) {
            EntityHandler.initialLoad()
        }

        if (isPostersEnabled()) {
            PosterHandler.initialLoad()
            PlacedPosterHandler.initialLoad()
        }

        NametagHandler.initialLoad()
        MenuTemplateHandler.initialLoad()
        ScoreboardPreviewHandler.initialLoad()
    }

    fun saveHandlers() {
        EntityHandler.saveData()

        if (isPostersEnabled()) {
            PosterHandler.saveData()
            PlacedPosterHandler.saveData()
        }

        MenuTemplateHandler.saveData()
    }

    private fun loadListeners() {
        ProtocolLibrary.getProtocolManager().addPacketListener(InventoryPacketAdapter)
        ProtocolLibrary.getProtocolManager().addPacketListener(MenuPacketAdapter)
        ProtocolLibrary.getProtocolManager().addPacketListener(EntityPacketAdapter)

        server.pluginManager.registerEvents(ButtonListeners, this)
        server.pluginManager.registerEvents(SelectItemListeners, this)
//        server.pluginManager.registerEvents(UUIDCacheListeners, this)
        server.pluginManager.registerEvents(EntityPacketAdapter, this) // this is not a mistake
        server.pluginManager.registerEvents(EntityWatcherListeners, this)
        server.pluginManager.registerEvents(LivingEntityListeners, this)
        server.pluginManager.registerEvents(EntityMoveTool, this)
        server.pluginManager.registerEvents(EntityPortTool, this)
        server.pluginManager.registerEvents(EntitySelectionTool, this)
        server.pluginManager.registerEvents(NpcEntityTeamUpdatesListeners, this)
        server.pluginManager.registerEvents(MenuAutoUpdater, this)
        server.pluginManager.registerEvents(ConsoleCommandLogListeners, this)
        server.pluginManager.registerEvents(NametagUpdatesListeners, this)
        server.pluginManager.registerEvents(ScoreboardListeners, this)
        server.pluginManager.registerEvents(VisibilityListeners, this)
        server.pluginManager.registerEvents(NametagListener, this)

        logger.info("Loaded listeners")
    }

    private fun loadTasks() {
        ScoreboardThread().start()

//        Tasks.asyncTimer(20L * 60L * 2, 20L * 60L * 2) {
//            systemSave()
//            uuidCache.load()
//        }

        Tasks.asyncTimer(NpcStrategyLogic, 20L, 20L)
        Tasks.asyncTimer(MenuAutoUpdater, 2L, 2L)
        Tasks.asyncTimer(Rainbow, 4L, 4L)
        Tasks.asyncTimer(UpdatingHologramTask, 10L, 10L)
        Tasks.asyncTimer(PersonalizedHologramTask, 10L, 10L)

        logger.info("Loaded tasks")
    }

    private fun systemSave() {
        val startAt = System.currentTimeMillis()

        PluginFramework.systemLog("Saving internal data...")

        for (world in Bukkit.getWorlds()) {
            try {
                Tasks.sync {
                    world.save()
                }
            } catch (exception: Exception) {
                ErrorHandler.generateErrorLog(
                    errorType = "systemSave-world",
                    event = mapOf("WorldName" to world.name),
                    exception = exception
                )

                PluginFramework.systemLog("${ChatColor.RED}Failed to save world ${world.name}!")
            }
        }

        try {
            saveHandlers()
        } catch (exception: Exception) {
            ErrorHandler.generateErrorLog(
                errorType = "systemSave-handlers",
                exception = exception
            )

            PluginFramework.systemLog("${ChatColor.RED}Failed to save handlers!")
        }

        val endAt = System.currentTimeMillis()

        PluginFramework.systemLog("Finished internal save in ${endAt - startAt}ms!")
    }

    /**
     * Hi-jack Bukkit's logging filter to suppress the server's complaints about commands being dispatched async
     */
    private fun filterConsoleLogs() {
        Bukkit.getLogger().setFilter {
            return@setFilter !it.message.contains("Command Dispatched Async")
                    && !it.message.contains("Please notify author of plugin")
        }
    }

    fun isPostersEnabled(): Boolean {
        return config.getBoolean("posters.enabled", false)
    }

}