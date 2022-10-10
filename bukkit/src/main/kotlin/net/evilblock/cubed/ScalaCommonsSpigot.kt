package net.evilblock.cubed

import com.comphenix.protocol.ProtocolLibrary
import gg.scala.aware.AwareBuilder
import gg.scala.aware.AwareHub
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.cache.uuid.impl.distribution.DistributedRedisUuidCacheTranslator
import gg.scala.cache.uuid.resolver.ResolverType
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.agnostic.sync.event.EventBus
import gg.scala.commons.agnostic.sync.event.events.ServerConnectedEvent
import gg.scala.commons.agnostic.sync.event.events.ServerDisconnectEvent
import gg.scala.commons.agnostic.sync.event.events.ServerLoadedEvent
import gg.scala.commons.agnostic.sync.server.SelfServerService
import gg.scala.commons.agnostic.sync.server.ServerContainer
import gg.scala.commons.agnostic.sync.server.impl.GameServer
import gg.scala.commons.agnostic.sync.server.region.Region
import gg.scala.commons.agnostic.sync.server.state.ServerState
import gg.scala.commons.annotations.container.ContainerDisable
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.config.annotations.ContainerConfig
import gg.scala.commons.protocol.ProtocolVersion
import gg.scala.commons.protocol.impl.ProtocolLibVersionProvider
import gg.scala.commons.proxy.Plugins
import gg.scala.commons.replacement.ReplacementGlobalContainer
import gg.scala.commons.replacement.Replacements
import gg.scala.commons.sentry.SentryConfig
import gg.scala.commons.sentry.SentryService
import gg.scala.commons.sign.Signs
import gg.scala.commons.tablist.TablistService
import gg.scala.commons.terminable.PlayerCompositeTerminable
import gg.scala.commons.util.Players
import me.lucko.helper.Commands
import me.lucko.helper.Schedulers
import me.lucko.helper.internal.HelperImplementationPlugin
import me.lucko.helper.internal.LoaderUtils
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
import net.evilblock.cubed.aware.AwareCloud
import net.evilblock.cubed.config.CommonsGeneralConfig
import net.evilblock.cubed.config.CommonsNametagConfig
import net.evilblock.cubed.config.CommonsPostersConfig
import net.evilblock.cubed.config.CommonsScoreboardConfig
import net.evilblock.cubed.config.CommonsTablistConfig
import net.evilblock.cubed.config.serversync.ServerSyncConfig
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntitySerializer
import net.evilblock.cubed.entity.animation.EntityAnimation
import net.evilblock.cubed.feature.DataStoreFeature
import net.evilblock.cubed.menu.protocol.InventoryPacketAdapter
import net.evilblock.cubed.menu.protocol.MenuPacketAdapter
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.menu.template.MenuTemplateButton
import net.evilblock.cubed.poster.PosterHandler
import net.evilblock.cubed.poster.placed.PlacedPosterHandler
import net.evilblock.cubed.serialize.BlockVectorAdapter
import net.evilblock.cubed.serialize.ItemStackAdapter
import net.evilblock.cubed.serialize.LocationAdapter
import net.evilblock.cubed.serialize.PotionEffectAdapter
import net.evilblock.cubed.serialize.VectorAdapter
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializer
import net.evilblock.cubed.statistic.ServerStatisticProviders
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.cubed.util.math.Numbers
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.util.BlockVector
import org.spigotmc.SpigotConfig
import java.util.*
import java.util.logging.Level

// plugin meta
@Plugin(
    name = "scala-commons",
    apiVersion = "1.18",
    depends = [
        PluginDependency("ProtocolLib"),
        PluginDependency("store-spigot", soft = true),
        PluginDependency("Vault", soft = true),
        PluginDependency("ViaVersion", soft = true),
        PluginDependency("spark", soft = true),
        PluginDependency("UnifiedMetrics", soft = true)
    ]
)
// configs
@ContainerConfig(
    value = "sentry",
    model = SentryConfig::class
)
@ContainerConfig(
    value = "settings",
    model = CommonsGeneralConfig::class
)
@ContainerConfig(
    value = "tablist",
    model = CommonsTablistConfig::class
)
@ContainerConfig(
    value = "nametags",
    model = CommonsNametagConfig::class
)
@ContainerConfig(
    value = "posters",
    model = CommonsPostersConfig::class
)
@ContainerConfig(
    value = "scoreboard",
    model = CommonsScoreboardConfig::class
)
// serversync
@ContainerConfig(
    value = "serversync",
    model = ServerSyncConfig::class
)
@HelperImplementationPlugin
class ScalaCommonsSpigot : ExtendedScalaPlugin()
{
    companion object
    {
        @JvmStatic
        val instance by Plugins.plugin<ScalaCommonsSpigot>()
    }

    val aware by lazy {
        AwareBuilder
            .of<AwareMessage>("UuidCache")
            .codec(AwareMessageCodec)
            .logger(logger)
            .build()
    }

    var versionProvider: ProtocolVersion = ProtocolLibVersionProvider

    @ContainerEnable
    fun containerEnable()
    {
        kotlin.runCatching {
            val config = config<CommonsGeneralConfig>()
            Reflection.DEBUG = config.debug

            createInfoCommand()

            if (!runSetupCheck())
                return@runCatching

            val default = TimeZone
                .getTimeZone(
                    config.timezone
                )

            flavor {
                inject(ReplacementGlobalContainer)
                inject(Replacements)
                inject(Signs)
                inject(TablistService)

                if (
                    server.pluginManager
                        .getPlugin("store-spigot") != null
                )
                {
                    inject(DataStoreFeature)
                }

                inject(PlayerCompositeTerminable)
            }

            TimeZone.setDefault(default)

            this.configureRedisServices()

            Serializers.create {
                registerTypeHierarchyAdapter(ItemStack::class.java, ItemStackAdapter())
                registerTypeHierarchyAdapter(Location::class.java, LocationAdapter())
                registerTypeHierarchyAdapter(Vector::class.java, VectorAdapter())
                registerTypeHierarchyAdapter(PotionEffect::class.java, PotionEffectAdapter())
                registerTypeHierarchyAdapter(BlockVector::class.java, BlockVectorAdapter())

                registerTypeAdapter(Entity::class.java, EntitySerializer)
                registerTypeAdapter(EntityAnimation::class.java, AbstractTypeSerializer<EntityAnimation>())
                registerTypeAdapter(MenuTemplate::class.java, AbstractTypeSerializer<MenuTemplate<*>>())
                registerTypeAdapter(MenuTemplateButton::class.java, AbstractTypeSerializer<MenuTemplateButton>())
            }

            if (isPostersEnabled())
            {
                flavor {
                    inject(PosterHandler)
                    inject(PlacedPosterHandler)
                }

                PosterHandler.configure()
                PlacedPosterHandler.configure()

                logger.info(
                    "[Posters] Configured poster functionality!"
                )
            }

            listOf(
                InventoryPacketAdapter,
                MenuPacketAdapter,
            ).forEach {
                ProtocolLibrary.getProtocolManager()
                    .addPacketListener(it)
            }

            Bukkit.getLogger().setFilter {
                !it.message.contains("Command Dispatched Async") &&
                        !it.message.contains("Please notify author of plugin")
            }

            kotlin.runCatching {
                GlowEnchantment.registerEnchant()
            }.onFailure {
                GlowEnchantment.ableToApplyGlowEnchant = false
                logger.info("We were unable to register our GlowEnchantment due to compatability issues.")
            }

            kotlin.runCatching {
                SentryService.initialize(this)
            }.onFailure {
                logger.log(Level.WARNING, "[Sentry] Failed to enable sentry support!", it)
            }
        }.onFailure {
            logger.log(
                Level.WARNING,
                "An exception was thrown during the startup process of Scala Commons, we're now shutting down.",
                it
            )
            server.shutdown()
        }
    }

    private var serverSync: SelfServerService<GameServer>? = null

    private fun configureRedisServices()
    {
        AwareHub.configure(
            AwareCloud.uri
        ) {
            Serializers.gson
        }

        ScalaStoreUuidCache
            .configure(
                DistributedRedisUuidCacheTranslator(aware),
                ScalaStoreUuidCache.resolve(
                    ResolverType.valueOf(
                        config<CommonsGeneralConfig>()
                            .uuidCacheResolverType
                    )
                )
            )

        val config = this.config<ServerSyncConfig>()

        if (config.enabled)
        {
            this.configureServerSync(config)
        }
    }

    private fun configureServerSync(config: ServerSyncConfig)
    {
        Serializers.create {
            GsonComponentSerializer.gson().populator().apply(this)
        }

        this.serverSync = ServerSync.configure(
            GameServer(config.serverId, config.serverGroups, Region.valueOf(config.region))
        ) {
            // server metadata
            kotlin.runCatching {
                setMetadata("server", "tps", ServerStatisticProviders.provider().ticksPerSecond())
                setMetadata("server", "cpu", ServerStatisticProviders.provider().cpuUsage())
            }.onFailure {
                setMetadata("server", "tps", 20.0)
                setMetadata("server", "cpu", 1.0)
            }

            setMetadata("server", "software", server.version)

            // online player metadata
            setMetadata("server", "online", server.onlinePlayers.size)
            setMetadata("server", "online-list", server.onlinePlayers.map { it.uniqueId })
            setMetadata("server", "online-max", server.maxPlayers)

            // whitelist
            setMetadata("server", "whitelisted", server.hasWhitelist())
            setMetadata("server", "whitelisted-players", server.whitelistedPlayers.map { it.uniqueId })
        }

        // There is no need to bind these to a local CompositeTerminable as we're
        // configuring this for the platform itself, not our plugin.
        EventBus.subscribe<ServerConnectedEvent>()
            .listen {
                Players.operators().forEach { player ->
                    player.sendMessage("${CC.GREEN}[ServerSync] ${CC.WHITE}${it.id}${CC.GRAY} connected.")
                }
            }

        EventBus.subscribe<ServerLoadedEvent>()
            .listen {
                val server = ServerContainer.getServer(it.id)
                    ?: return@listen

                val formatted = Numbers.format(
                    System.currentTimeMillis() - server.initialHeartbeat
                )

                Players.operators().forEach { player ->
                    player.sendMessage("${CC.GREEN}[ServerSync] ${CC.WHITE}${it.id}${CC.GRAY} loaded ${CC.D_GRAY}(${formatted}ms)${CC.GRAY}.")
                }
            }

        EventBus.subscribe<ServerDisconnectEvent>()
            .listen {
                Players.operators().forEach { player ->
                    player.sendMessage("${CC.GREEN}[ServerSync] ${CC.RED}${it.id}${CC.GRAY} disconnected. ${CC.D_GRAY}(${it.reason})")
                }
            }

        Schedulers.sync().run {
            // configure this once the server's started up completely
            ServerSync.local.state = ServerState.Loaded

            val event = ServerLoadedEvent(
                ServerSync.local.id,
                ServerSync.local.javaClass.typeName
            )

            event.push()
        }

        this.logger.info("[ServerSync] Configured server sync with identifier: ${config.serverId}.")
    }

    private fun createInfoCommand()
    {
        val config = config<CommonsGeneralConfig>()

        if (config.infoCommand)
        {
            Commands.create()
                .handler { context ->
                    val implementationPlugins = LoaderUtils
                        .getHelperPlugins()
                        .sortedBy { it.name }

                    implementationPlugins.forEach {
                        context.sender().sendMessage(
                            "&a[helper] &7Running &b${it.name}&7 version &e${it.description.version}"
                        )
                    }
                }
                .registerAndBind(
                    this, "helper"
                )
        }
    }

    @ContainerDisable
    fun containerDisable()
    {
        if (isPostersEnabled())
        {
            kotlin.runCatching {
                PosterHandler.saveData()
                PlacedPosterHandler.saveData()

                PlacedPosterHandler.onDisable()
            }.onFailure {
                logger.info("[Posters] Failed to save poster data!")
            }
        }

        this.serverSync
            ?.disconnect(
                reason = "manually stopped"
            )
            ?.toCompletableFuture()
            ?.join()

        ScalaStoreUuidCache
            .useTranslator {
                this.close()
            }

        AwareHub.close()
    }

    /**
     * Runs important checks to ensure this server instance is properly setup.
     * If any check fails, the server automatically shuts down.
     *
     * @return true if checks are passed, otherwise false
     */
    private fun runSetupCheck(): Boolean
    {
        if (this.config<CommonsGeneralConfig>().ignoreServerCheck)
        {
            return true
        }

        val onlineAndProxy = server.onlineMode && SpigotConfig.bungee
        val offlineAndNoProxy = !server.onlineMode && !SpigotConfig.bungee

        if (onlineAndProxy || offlineAndNoProxy)
        {
            this.logger.severe(" ")
            this.logger.severe(" Failed environment check!")
            this.logger.severe("  Online mode: ${server.onlineMode}")
            this.logger.severe("  Proxy mode: ${SpigotConfig.bungee}")
            this.logger.severe(" ")

            server.shutdown()
            return false
        }

        return true
    }

    private fun isPostersEnabled(): Boolean
    {
        return config<CommonsPostersConfig>()
            .enabled
    }
}
