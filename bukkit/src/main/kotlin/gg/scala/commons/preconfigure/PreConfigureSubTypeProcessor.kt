package gg.scala.commons.preconfigure

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.EagerInjection
import gg.scala.commons.persist.ProfileOrchestrator
import gg.scala.flavor.service.Service
import org.reflections.scanners.Scanners
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 4/23/2022
 */
object PreConfigureSubTypeProcessor
{
    val processors =
        mutableMapOf<KClass<out Any>, (Any) -> Unit>()

    init
    {
        this.register<ProfileOrchestrator<*>> {
            it.subscribe()
        }
    }

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
                .reflections
                .get(
                    Scanners.SubTypes
                        .with(type.java)
                        .`as`(Class::class.java)
                )
                .filter {
                    it.isAnnotationPresent(Service::class.java)
                }

            for (service in services)
            {
                val instance = service.objectInstance()
                    ?: service
                        .getDeclaredConstructor()
                        .newInstance()

                if (instance.javaClass.isAnnotationPresent(EagerInjection::class.java))
                {
                    plugin.flavor().inject(instance)
                }

                processor.invoke(instance)
            }
        }
    }

    fun Class<*>.objectInstance(): Any?
    {
        return runCatching {
            getDeclaredField("INSTANCE").get(null)
        }.getOrNull()
    }
}
