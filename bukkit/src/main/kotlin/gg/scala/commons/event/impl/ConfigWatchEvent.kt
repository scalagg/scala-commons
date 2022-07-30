package gg.scala.commons.event.impl

import gg.scala.commons.config.ConfigContainer
import gg.scala.commons.event.StatefulEvent

/**
 * @author GrowlyX
 * @since 7/30/2022
 */
class ConfigWatchEvent(
    val container: ConfigContainer
) : StatefulEvent()
