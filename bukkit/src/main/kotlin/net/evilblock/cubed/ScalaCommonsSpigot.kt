package net.evilblock.cubed

import com.comphenix.protocol.ProtocolLibrary
import gg.scala.aware.AwareBuilder
import gg.scala.aware.AwareHub
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.cache.uuid.impl.distribution.DistributedRedisUuidCacheTranslator
import gg.scala.commons.ExtendedScalaPlugin
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
import me.lucko.helper.Commands
import me.lucko.helper.internal.HelperImplementationPlugin
import me.lucko.helper.internal.LoaderUtils
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
import me.lucko.helper.utils.Players
import net.evilblock.cubed.aware.AwareCloud
import net.evilblock.cubed.config.CommonsGeneralConfig
import net.evilblock.cubed.config.CommonsNametagConfig
import net.evilblock.cubed.config.CommonsPostersConfig
import net.evilblock.cubed.config.CommonsScoreboardConfig
import net.evilblock.cubed.config.CommonsTablistConfig
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
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
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
    apiVersion = "1.19",
    authors = ["GrowlyX", "joeleoli"],
    description = "Core utilities for Scala spigot plugins.",
    depends = [
        PluginDependency("ProtocolLib"),
        PluginDependency("store-spigot", soft = true),
        PluginDependency("Vault", soft = true),
        PluginDependency("ViaVersion", soft = true),
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

            Reflection.DEBUG =
                config.debug

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

                GsonComponentSerializer.gson().populator().apply(this)
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

            AwareHub.configure(
                AwareCloud.uri
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
        val config = config<CommonsGeneralConfig>()

        if (config.infoCommand)
        {
            Commands.create()
                .handler { context ->
                    val implementationPlugins = LoaderUtils
                        .getHelperPlugins()

                    implementationPlugins.forEach {
                        Players.msg(
                            context.sender(),
                            "&7[${CC.GREEN}helper&7] &7Running ${
                                CC.GOLD
                            }${it.name}${
                                CC.GRAY
                            } version ${
                                CC.YELLOW
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

        this.aware.shutdown()
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
