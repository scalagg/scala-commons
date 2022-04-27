package net.evilblock.cubed

import com.comphenix.protocol.ProtocolLibrary
import gg.scala.aware.AwareBuilder
import gg.scala.aware.AwareHub
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.aware.uri.WrappedAwareUri
import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.cache.uuid.impl.distribution.DistributedRedisUuidCacheTranslator
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.container.ContainerDisable
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.config.annotations.ContainerConfig
import gg.scala.commons.proxy.Plugins
import gg.scala.commons.sentry.SentryConfig
import gg.scala.commons.sentry.SentryService
import gg.scala.commons.serializer.ComponentSerializer
import gg.scala.store.serializer.serializers.GsonSerializer
import gg.scala.store.spigot.ScalaDataStoreSpigot
import me.lucko.helper.Commands
import me.lucko.helper.internal.HelperImplementationPlugin
import me.lucko.helper.internal.LoaderUtils
import me.lucko.helper.maven.MavenLibrary
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
import me.lucko.helper.text.serializer.ComponentSerializers
import me.lucko.helper.utils.Players
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntitySerializer
import net.evilblock.cubed.entity.animation.EntityAnimation
import net.evilblock.cubed.menu.protocol.InventoryPacketAdapter
import net.evilblock.cubed.menu.protocol.MenuPacketAdapter
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.menu.template.MenuTemplateButton
import net.evilblock.cubed.poster.PosterHandler
import net.evilblock.cubed.poster.placed.PlacedPosterHandler
import net.evilblock.cubed.scoreboard.ScoreboardThread
import net.evilblock.cubed.serialize.BlockVectorAdapter
import net.evilblock.cubed.serialize.ItemStackAdapter
import net.evilblock.cubed.serialize.LocationAdapter
import net.evilblock.cubed.serialize.VectorAdapter
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializer
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.ServerVersion
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BlockVector
import org.spigotmc.SpigotConfig
import java.util.*
import java.util.logging.Level

@Plugin(
    name = "scala-commons",
    apiVersion = "1.18",
    description = "Core utilities for Scala spigot plugins.",
    depends = [
        PluginDependency("helper"),
        PluginDependency("store-spigot"),
        PluginDependency("ProtocolLib"),
        PluginDependency("Vault", soft = true),
    ]
)
@ContainerConfig(
    value = "sentry",
    model = SentryConfig::class
)
@MavenLibrary(
    groupId = "org.jetbrains.kotlin",
    artifactId = "kotlin-reflect",
    version = "1.6.21"
)
@HelperImplementationPlugin
class Cubed : ExtendedScalaPlugin()
{
    companion object
    {
        @JvmStatic
        val instance by Plugins.plugin<Cubed>()
    }

    private val aware by lazy {
        AwareBuilder
            .of<AwareMessage>("UuidCache")
            .codec(AwareMessageCodec)
            .logger(logger)
            .build()
    }

    @ContainerEnable
    fun containerEnable()
    {
        kotlin.runCatching {
            Reflection.DEBUG = config
                .getBoolean("debug", false)

            saveDefaultConfig()
            createInfoCommand()

            if (!runSetupCheck())
                return@runCatching

            val default = TimeZone
                .getTimeZone(
                    config.getString("timezone", "EST")
                )

            TimeZone.setDefault(default)

            Serializers.useGsonBuilderThenRebuild { builder ->
                builder
                    .registerTypeHierarchyAdapter(ItemStack::class.java, ItemStackAdapter())
                    .registerTypeHierarchyAdapter(Location::class.java, LocationAdapter())
                    .registerTypeHierarchyAdapter(Vector::class.java, VectorAdapter())
                    .registerTypeHierarchyAdapter(BlockVector::class.java, BlockVectorAdapter())
                    .registerTypeAdapter(
                        Component::class.java,
                        ComponentSerializer
                    )
                    .registerTypeAdapter(Entity::class.java, EntitySerializer)
                    .registerTypeAdapter(EntityAnimation::class.java, AbstractTypeSerializer<EntityAnimation>())
                    .registerTypeAdapter(MenuTemplate::class.java, AbstractTypeSerializer<MenuTemplate<*>>())
                    .registerTypeAdapter(MenuTemplateButton::class.java, AbstractTypeSerializer<MenuTemplateButton>())
            }

            GsonSerializer
                .provideCustomGson {
                    Serializers.gson
                }

            if (isPostersEnabled())
            {
                PosterHandler.configure()
                PlacedPosterHandler.configure()

                logger.info(
                    "[Posters] Configured poster functionality!"
                )
            }

            registerPacketListeners()

            Bukkit.getLogger().setFilter {
                return@setFilter !it.message.contains("Command Dispatched Async")
                        && !it.message.contains("Please notify author of plugin")
            }

            val storeRedis = ScalaDataStoreSpigot
                .INSTANCE.redis

            AwareHub.configure(
                WrappedAwareUri(
                    storeRedis.hostname, storeRedis.port,
                    storeRedis.password, 0
                )
            ) {
                Serializers.gson
            }

            ScalaStoreUuidCache.configure(
                DistributedRedisUuidCacheTranslator(aware)
            )

            kotlin.runCatching {
                GlowEnchantment.registerEnchant()
            }.onFailure {
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
                "An exception was thrown during the startup process of Cubed, we're now shutting down.",
                it
            )
            server.shutdown()
        }
    }

    private fun createInfoCommand()
    {
        if (!config.getBoolean("info-command", true))
            return

        Commands.create()
            .handler { c ->
                val implementationPlugins = LoaderUtils
                    .getHelperPlugins()

                implementationPlugins.forEach {
                    Players.msg(
                        c.sender(),
                        "&7[&${CC.PRI}helper&7] &7Running ${
                            CC.SEC
                        }${it.name}${
                            CC.GRAY
                        } version ${
                            CC.PRI
                        }${
                            it.description.version
                        }&7."
                    )
                }
            }
            .registerAndBind(
                this, "helper"
            )
    }

    @ContainerDisable
    fun containerDisable()
    {
        kotlin.runCatching {
            if (isPostersEnabled())
            {
                PosterHandler.saveData()
                PlacedPosterHandler.saveData()

                PlacedPosterHandler.onDisable()
            }

            kotlin.runCatching {
                aware.shutdown()
            }
        }.onFailure {
            logger.log(Level.WARNING, "An exception was thrown during disable process.", it)
        }
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
            logger.severe(" ")
            logger.severe(" Failed environment check!")
            logger.severe("  Online mode: ${server.onlineMode}")
            logger.severe("  Proxy mode: ${SpigotConfig.bungee}")
            logger.severe(" ")

            server.shutdown()
            false
        } else
        {
            true
        }
    }

    private fun registerPacketListeners()
    {
        listOf(
            InventoryPacketAdapter,
            MenuPacketAdapter,
        ).forEach {
            ProtocolLibrary.getProtocolManager()
                .addPacketListener(it)
        }
    }

    private fun isPostersEnabled(): Boolean
    {
        return config.getBoolean("posters.enabled", false)
    }
}
