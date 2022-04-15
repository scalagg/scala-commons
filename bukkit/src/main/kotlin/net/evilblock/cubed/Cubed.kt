/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

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
import gg.scala.flavor.Flavor
import gg.scala.flavor.FlavorOptions
import gg.scala.store.serializer.serializers.GsonSerializer
import gg.scala.store.spigot.ScalaDataStoreSpigot
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
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
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BlockVector
import org.spigotmc.SpigotConfig
import java.util.*
import java.util.logging.Level

@Plugin(
    name = "Cubed",
    apiVersion = "1.18",
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
    }

    private val aware by lazy {
        AwareBuilder
            .of<AwareMessage>("UuidCache")
            .codec(AwareMessageCodec)
            .logger(logger)
            .build()
    }

    private val flavor by lazy {
        Flavor.create<Cubed>(
            FlavorOptions(logger)
        )
    }

    @ContainerEnable
    fun containerEnable()
    {
        kotlin.runCatching {
            instance = this

            Reflection.DEBUG = config
                .getBoolean("debug", false)

            saveDefaultConfig()

            if (!runSetupCheck())
                return@runCatching

            CC.setup(
                CC.GREEN, CC.YELLOW
            )

            TimeZone.setDefault(
                TimeZone.getTimeZone(config
                    .getString("timezone", "EST")
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

            flavor.bind<Cubed>() to this
            flavor.startup()

            if (isPostersEnabled())
            {
                PosterHandler.configure()
                PlacedPosterHandler.configure()
            }

            registerPacketListeners()
            ScoreboardThread().start()

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
        }.onFailure {
            logger.log(Level.WARNING, "An exception was thrown during the startup process of Cubed, we're now shutting down.", it)
            server.shutdown()
        }
    }

    @ContainerDisable
    fun containerDisable()
    {
        kotlin.runCatching {
            flavor.close()

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
