package gg.scala.commons.config

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.config.annotations.ContainerConfig
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
    val annotation: ContainerConfig
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
        }.onFailure {
            this.plugin.logger.log(Level.WARNING, "[Config] Failed to load config by name \"${annotation.value}\"!", it)
        }
    }
}
