package gg.scala.banana.subscribe

import gg.scala.banana.annotate.Subscribe
import java.lang.reflect.Method

/**
 * Defines information for a specific [Subscribe] method.
 *
 * @author GrowlyX
 * @since 9/19/2021
 */
data class BananaSubscriptionData(
    val clazz: Any, val method: Method, val subscribe: Subscribe
)
