package gg.scala.commons.config

import gg.scala.aware.AwareBuilder
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.config.annotations.Config
import gg.scala.commons.config.annotations.ContainerConfig
import gg.scala.commons.config.convert.ConvertedConfig
import gg.scala.commons.config.convert.annotations.ConfigConverted
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

    fun <T : Any> container(clazz: Class<T>): ConfigContainer
    {
        return this.configContainers[clazz.kotlin]!!
    }

    inline fun <reified T> container(): ConfigContainer
    {
        return this.configContainers[T::class]!!
    }

    val waitingLateInject =
        hashSetOf<ContainerConfig>()

    val waitingLateInjectCustom =
        hashSetOf<ConvertedConfig>()

    val aware by lazy {
        AwareBuilder.of<AwareMessage>("config-sync")
            .logger(plugin.logger)
            .codec(AwareMessageCodec)
            .build()
    }

    override fun inject()
    {
        val configs = this.plugin::class.java
            .getAnnotationsByType(
                ContainerConfig::class.java
            )
            .filter {
                !it.lateInject
            }

        val additional = this.plugin.packageIndexer
            .getTypesAnnotatedWith<Config>()
            .map { it to it.getAnnotation(Config::class.java) }
            .filter {
                !it.second.lateInject
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

        for (config in additional)
        {
            val container = ContainerConfig(
                config.second.value, config.first.kotlin,
                config.second.lateInject, config.second.crossSync
            )

            if (config.second.lateInject)
            {
                waitingLateInject.add(container)
                continue
            }

            inject(container)
        }

        this.plugin.flavor().reflections
            .getTypesAnnotatedWith<ConfigConverted>()
            .forEach {
                val converted = ConvertedConfig(
                    plugin, it.kotlin, this
                )

                if (converted.annotation.lateInject)
                {
                    waitingLateInjectCustom.add(converted)
                    return@forEach
                }

                customInjection(converted)
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
            ConfigContainer(plugin, configFactory, config, this)
    }

    fun customInjection(container: ConfigContainer)
    {
        plugin.logger.info(
            "[Config] Injected config ${
                container.annotation.value
            } into ${
                container.annotation.model.simpleName
            }."
        )

        configContainers[container.annotation.model] = container
    }

    var initialLoaded = false

    override fun loadAll()
    {
        for (configContainer in this.configContainers)
        {
            configContainer.value.postInject()
            configContainer.value.load()

            configContainer.value.model
                ?.apply {
                    plugin.flavor().binders.add(
                        FlavorBinder(configContainer.key) to this
                    )
                }
        }

        if (
            !initialLoaded && this
                .configContainers.values.map {
                    it.annotation
                }.any {
                    it.crossSync
                }
        )
        {
            this.aware.connect()
                .toCompletableFuture()
                .join()

            this.initialLoaded = true
        }

        plugin.flavor().inject(plugin)
    }
}
