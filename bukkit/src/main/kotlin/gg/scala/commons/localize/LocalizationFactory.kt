package gg.scala.commons.localize

import java.io.File
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 3/30/2022
 */
class LocalizationFactory(
    val container: KClass<*>,
    val identifier: String,
    val directory: File,
    val loader: LocalizationLoader
)
{
    val containers =
        mutableMapOf<LocalizationContext, LocalizationContainer>()

    fun load()
    {
        LocalizationContext.values().forEach {
            val container =
                LocalizationContainer(this, it, loader)

            if (container.validate())
            {
                container.preLoad()
                containers[it] = container
            }
        }
    }
}
