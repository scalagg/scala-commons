package gg.scala.commons.proxy

import gg.scala.commons.ExtendedScalaPlugin
import kotlin.properties.ReadWriteProperty

/**
 * @author GrowlyX
 * @since 4/17/2022
 */
object Plugins
{
    inline fun <reified T : ExtendedScalaPlugin> plugin(): ReadWriteProperty<Any, T>
    {
        return PluginsReadWriteProperty(T::class)
    }
}
