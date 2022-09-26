package gg.scala.commons.binder

import gg.scala.commons.ExtendedScalaPlugin
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

        val pluginsLoaded = Bukkit
            .getPluginManager().plugins
            .filter {
                it.isEnabled
            }

        for (entry in pluginsLoaded)
        {
            bind(entry)
                .populate {
                    this.annotated<Named> {
                        it.value == entry.name
                    }
                }
                .to(entry::class)
                .apply {
                    if (entry is ExtendedScalaPlugin)
                        to<ExtendedScalaPlugin>()
                }
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
