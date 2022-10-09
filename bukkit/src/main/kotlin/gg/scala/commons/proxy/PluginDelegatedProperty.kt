package gg.scala.commons.proxy

import gg.scala.commons.ExtendedScalaPlugin
import org.bukkit.plugin.java.JavaPlugin
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Lazily retrieves a targeted [JavaPlugin]
 * to act as an "instance" field.
 *
 * Gets rid of the need to set the
 * instance field on startup.
 *
 * @author GrowlyX
 * @since 4/17/2022
 */
class PluginDelegatedProperty<T : ExtendedScalaPlugin>(
    private val typeClass: KClass<T>
) : ReadOnlyProperty<Any, T>
{
    var internal: T? = null

    override fun getValue(
        thisRef: Any,
        property: KProperty<*>
    ): T
    {
        if (this.internal == null)
        {
            this.internal = JavaPlugin
                .getPlugin(this.typeClass.java)
        }

        return this.internal!!
    }
}
