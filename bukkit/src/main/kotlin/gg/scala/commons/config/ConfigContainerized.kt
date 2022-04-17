package gg.scala.commons.config

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.config.annotations.ContainerConfig
import xyz.mkotb.configapi.ConfigFactory
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 4/17/2022
 */
class ConfigContainerized(
    val plugin: ExtendedScalaPlugin
)
{
    private val configFactory by lazy {
        ConfigFactory.newFactory(plugin)
    }

    val configContainers =
        mutableMapOf<KClass<out Any>, ConfigContainer>()

    inline fun <reified T> container(): ConfigContainer
    {
        return this.configContainers[T::class]!!
    }

    fun inject()
    {
        plugin.flavor {
            this.reflections
                .getTypesAnnotatedWith<ContainerConfig>()
                .forEach {
                    val configs = it
                        .getAnnotationsByType(ContainerConfig::class.java)
                        .filter { config ->
                            !configContainers.contains(config.model)
                        }

                    for (config in configs)
                    {
                        configContainers[config.model] =
                            ConfigContainer(plugin, configFactory, config)
                    }
                }
        }
    }


    fun loadAll()
    {
        for (configContainer in this.configContainers)
        {
            configContainer.value.load()
        }
    }
}
