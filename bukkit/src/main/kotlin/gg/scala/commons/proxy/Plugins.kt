package gg.scala.commons.proxy

import gg.scala.commons.ExtendedScalaPlugin
import kotlin.properties.ReadOnlyProperty

/**
 * @author GrowlyX
 * @since 4/17/2022
 */
object Plugins
{
    inline fun <reified T : ExtendedScalaPlugin> plugin(): ReadOnlyProperty<Any, T>
    {
        return PluginDelegatedProperty(T::class)
    }
}
