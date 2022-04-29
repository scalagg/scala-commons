package gg.scala.commons.config

import gg.scala.aware.message.AwareMessage
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.config.annotations.ContainerConfig
import gg.scala.flavor.FlavorBinder
import net.evilblock.cubed.serializers.Serializers
import xyz.mkotb.configapi.ConfigFactory
import java.util.logging.Level
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 4/17/2022
 */
class ConfigContainer(
    val plugin: ExtendedScalaPlugin,
    private val factory: ConfigFactory,
    val annotation: ContainerConfig,
    private val containerized: ConfigContainerized
)
{
    var model: Any? = null

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getModelAs(kClass: KClass<T>): T
    {
        return model as T
    }

    inline fun <reified T> getModelAs(): T
    {
        return model as T
    }

    private var injected = false

    fun postInject()
    {
        if (injected)
            return

        if (this.annotation.crossSync)
        {
            this.containerized.aware.listen(
                this.annotation.model.java.simpleName
            ) {
                val content = this.retrieve(
                    "config", annotation.model
                )

                model = content
                save(crossSync = false)

                plugin.logger.info(
                    "[Config] [Sync] Synchronized config ${
                        annotation.value
                    } injected into ${
                        annotation.model.simpleName
                    }."
                )
            }

            plugin.logger.info(
                "[Config] [Sync] Listening to incoming requests for config ${
                    annotation.value
                } injected into ${
                    annotation.model.simpleName
                }."
            )
        }

        this.injected = true
    }

    fun save(
        crossSync: Boolean = true
    )
    {
        if (this.model == null)
        {
            throw IllegalStateException("Model has not been loaded yet")
        }

        kotlin.runCatching {
            this.factory.save(annotation.value, model)

            if (crossSync && this.annotation.crossSync)
            {
                AwareMessage.of(
                    this.annotation.model.java.simpleName,
                    this.containerized.aware,
                    "config" to this.model!!
                ).publish()
            }
        }.onFailure {
            this.plugin.logger.log(
                Level.WARNING, "[Config] Failed to load config by name \"${annotation.value}\"!", it
            )
        }.onSuccess {
            this.plugin.logger.info("[Config] Saved config ${
                annotation.value
            } injected into ${
                annotation.model.simpleName
            }.")
        }
    }

    fun load()
    {
        kotlin.runCatching {
            this.model = factory.fromFile(
                annotation.value, annotation.model.java
            )
        }.onSuccess {
            this.plugin.logger.info("[Config] Loaded config ${
                annotation.value
            } injected into ${
                annotation.model.simpleName
            }.")

            plugin.flavor().binders.add(
                FlavorBinder(annotation.model) to getModelAs()
            )
        }.onFailure {
            this.plugin.logger.log(
                Level.WARNING, "[Config] Failed to load config by name \"${annotation.value}\"!", it
            )
        }
    }
}
