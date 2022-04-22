package gg.scala.commons.config

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.config.annotations.ContainerConfig
import gg.scala.commons.model.Containerized
import gg.scala.flavor.FlavorBinder
import xyz.mkotb.configapi.ConfigFactory
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 4/17/2022
 */
class ConfigContainerized(
    val plugin: ExtendedScalaPlugin
) : Containerized
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

    val waitingLateInject =
        HashSet<ContainerConfig>()

    override fun inject()
    {
        val configs = this.plugin::class.java
            .getAnnotationsByType(
                ContainerConfig::class.java
            )
            .filter {
                !it.lateInject
            }

        for (config in configs)
        {
            if (config.lateInject)
            {
                waitingLateInject.add(config)
                continue
            }

            inject(config)
        }
    }

    fun inject(config: ContainerConfig)
    {
        plugin.logger.info(
            "[Config] Injected config ${
                config.value
            } into ${
                config.model.simpleName
            }."
        )

        configContainers[config.model] =
            ConfigContainer(plugin, configFactory, config)
    }

    override fun loadAll()
    {
        for (configContainer in this.configContainers)
        {
            configContainer.value.load()
        }
    }
}
