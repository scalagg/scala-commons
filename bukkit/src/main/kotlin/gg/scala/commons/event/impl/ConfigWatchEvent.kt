package gg.scala.commons.event.impl

import gg.scala.commons.config.ConfigContainer
import gg.scala.commons.event.StatefulEvent

class ConfigWatchEvent(
    val container: ConfigContainer
) : StatefulEvent()
