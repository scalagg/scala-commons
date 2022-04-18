package gg.scala.commons.proxy

import org.bukkit.plugin.java.JavaPlugin
import kotlin.properties.ReadWriteProperty

/**
 * @author GrowlyX
 * @since 4/17/2022
 */
object Plugins
{
    inline fun <reified T : JavaPlugin> get(): ReadWriteProperty<Any, T>
    {
        return PluginsReadWriteProperty(T::class)
    }
}
