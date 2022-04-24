package gg.scala.commons.proxy

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.plugin.PluginBridge
import org.bukkit.plugin.java.JavaPlugin
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Lazily retrieves a targeted [JavaPlugin]
 * to act as an "instance" field.
 *
 * Gets rid of the need to set the
 * instance's field on startup.
 *
 * @author GrowlyX
 * @since 4/17/2022
 */
class PluginsReadWriteProperty<T : ExtendedScalaPlugin>(
    private val typeClass: KClass<T>
) : ReadWriteProperty<Any, T>
{
    var internal: T? = null

    override fun getValue(
        thisRef: Any,
        property: KProperty<*>
    ): T
    {
        if (this.internal == null)
        {
            kotlin.runCatching {
                this.internal = PluginBridge
                    .plugin(this.typeClass)
            }.onFailure {
                this.internal = JavaPlugin
                    .getPlugin(this.typeClass.java)
            }
        }

        return this.internal as T
    }

    override fun setValue(
        thisRef: Any,
        property: KProperty<*>,
        value: T
    )
    {
        throw IllegalStateException(
            "No. You cannot do the set."
        )
    }
}
