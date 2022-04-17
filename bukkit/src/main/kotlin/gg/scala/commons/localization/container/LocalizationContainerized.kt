package gg.scala.commons.localization.container

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.localization.container.annotations.Localized
import gg.scala.commons.model.Containerized

/**
 * @author GrowlyX
 * @since 4/17/2022
 */
class LocalizationContainerized(
    val plugin: ExtendedScalaPlugin
) : Containerized
{
    val configContainers =
        mutableMapOf<String, LocalizationContainerContextual>()

    fun container(
        identifier: String
    ): LocalizationContainerContextual
    {
        return this.configContainers[identifier]!!
    }

    override fun inject()
    {
        plugin.flavor {
            this.reflections
                .getTypesAnnotatedWith<Localized>()
                .forEach {
                    val configs = it
                        .getAnnotationsByType(Localized::class.java)
                        .filter { config ->
                            !configContainers.contains(config.value)
                        }

                    for (config in configs)
                    {
                        configContainers[config.value] =
                            LocalizationContainerContextual(
                                plugin::class, config.value,
                                plugin.dataFolder.apply {
                                    if (!exists())
                                        mkdir()
                                }, config.loader
                            )
                    }
                }
        }
    }

    override fun loadAll()
    {
        configContainers.forEach {
            it.value.load()
        }
    }
}
