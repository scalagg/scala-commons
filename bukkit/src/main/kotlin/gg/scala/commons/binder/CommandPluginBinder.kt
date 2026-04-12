package gg.scala.commons.binder

import co.aikar.commands.BukkitCommandManager
import co.aikar.commands.PaperCommandManager
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.command.ScalaCommandManager
import gg.scala.flavor.binder.FlavorBinderContainer
import gg.scala.flavor.inject.condition.Named
import org.bukkit.Bukkit

class CommandPluginBinder(
    private val plugin: ExtendedScalaPlugin
) : FlavorBinderContainer()
{
    override fun populate()
    {
        bind(plugin.commandManager)
            .to<ScalaCommandManager>()
            .to<PaperCommandManager>()
            .to<BukkitCommandManager>()
            .bind()

        val pluginsLoaded = Bukkit
            .getPluginManager().plugins
            .filter {
                it.isEnabled
            }
            .filterIsInstance<ExtendedScalaPlugin>()

        for (entry in pluginsLoaded)
        {
            bind(entry.commandManager)
                .populate {
                    this.annotated<Named> {
                        it.value == entry.name
                    }
                }
                .to<ScalaCommandManager>()
                .to<PaperCommandManager>()
                .to<BukkitCommandManager>()
                .bind()
        }
    }
}
