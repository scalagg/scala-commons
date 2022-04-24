package gg.scala.commons.preconfigure

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 4/23/2022
 */
object PreConfigureSubTypeProcessor
{
    val processors =
        mutableMapOf<KClass<out Any>, (Any) -> Unit>()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> register(
        noinline processor: (T) -> Unit
    )
    {
        this.processors[T::class] =
            processor as (Any) -> Unit
    }

    fun processLocalSubTypeServices(
        plugin: ExtendedScalaPlugin
    )
    {
        processors.forEach { (type, processor) ->
            val services = plugin.packageIndexer
                .reflections.getSubTypesOf(
                    type::class.java
                )
                .filter {
                    it.isAnnotationPresent(Service::class.java)
                }

            for (service in services)
            {
                processor.invoke(service.kotlin.objectInstance!!)
            }
        }
    }
}
