package gg.scala.commons

import co.aikar.commands.BukkitCommandManager
import co.aikar.commands.CommandManager
import co.aikar.commands.PaperCommandManager
import gg.scala.commons.command.ScalaCommandManager
import gg.scala.commons.plugin.PluginBridge
import gg.scala.flavor.binder.FlavorBinderContainer
import gg.scala.flavor.inject.condition.Named
import net.minecraft.server.v1_8_R3.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.messaging.Messenger
import org.bukkit.scheduler.BukkitScheduler

/**
 * @author GrowlyX
 * @since 9/6/2022
 */
class DefaultPluginBinder(
    private val plugin: ExtendedScalaPlugin
) : FlavorBinderContainer()
{
    override fun populate()
    {
        bind(plugin)
            .to(plugin::class)
            .to<ExtendedScalaPlugin>()
            .to<JavaPlugin>()
            .to<Plugin>()
            .bind()

        for (entry in PluginBridge.cache.asMap())
        {
            bind(entry.value)
                .populate {
                    this.annotated<Named> {
                        it.value == entry.value.name
                    }
                }
                .to(entry.value::class)
                .to<ExtendedScalaPlugin>()
                .to<JavaPlugin>()
                .to<Plugin>()
                .bind()
        }

        bind(Bukkit.getServer())
            .to<Server>()
            .to<MinecraftServer>()
            .bind()

        bind(Bukkit.getPluginManager())
            .to<PluginManager>()
            .bind()

        bind(Bukkit.getBukkitVersion())
            .to<String>()
            .populate {
                this.annotated<Named> {
                    it.value == "bukkit:version"
                }
            }
            .bind()

        bind(Bukkit.getScheduler())
            .to<BukkitScheduler>()
            .bind()

        bind(Bukkit.getMessenger())
            .to<Messenger>()
            .bind()
    }
}
