package gg.scala.commons.localize

import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 3/30/2022
 */
object Localization
{
    val factories = mutableMapOf<KClass<*>, LocalizationContainer>()
}
