package gg.scala.commons.proxy

import gg.scala.commons.ExtendedScalaPlugin
import org.bukkit.plugin.java.JavaPlugin
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

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
