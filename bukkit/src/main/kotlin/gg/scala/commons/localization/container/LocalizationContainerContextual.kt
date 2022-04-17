package gg.scala.commons.localization.container

import gg.scala.commons.localization.LocalizationContext
import gg.scala.commons.localization.LocalizationLoader
import java.io.File
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 3/30/2022
 */
class LocalizationContainerContextual(
    val container: KClass<*>,
    val identifier: String,
    val directory: File,
    private val loader: LocalizationLoader
)
{
    private val containers =
        mutableMapOf<LocalizationContext, LocalizationContainer>()

    fun contextual(
        context: LocalizationContext
    ): LocalizationContainer
    {
        return containers[context]!!
    }

    fun load()
    {
        LocalizationContext.values().forEach {
            val container =
                LocalizationContainer(
                    this, it, loader
                )

            container.preLoad()
            containers[it] = container
        }
    }
}
