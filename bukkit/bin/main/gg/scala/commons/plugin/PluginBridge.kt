package gg.scala.commons.plugin

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import gg.scala.commons.ExtendedScalaPlugin
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 4/23/2022
 */
object PluginBridge
{
    val cache: Cache<KClass<out ExtendedScalaPlugin>, ExtendedScalaPlugin> =
        CacheBuilder.newBuilder().build()

    fun install(
        plugin: ExtendedScalaPlugin
    )
    {
        this.cache.put(plugin::class, plugin)
    }

    fun invalidate(
        plugin: ExtendedScalaPlugin
    )
    {
        this.cache.invalidate(plugin)
    }

    fun <T : ExtendedScalaPlugin> plugin(
        kClass: KClass<T>
    ): T
    {
        val plugin = this.cache.get(kClass) { null }
            ?: throw IllegalArgumentException(
                "Plugin isn't installed in cache."
            )

        return plugin as T
    }

    inline fun <reified T : ExtendedScalaPlugin> plugin(): T
    {
        return plugin(T::class)
    }
}
