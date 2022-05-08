package gg.scala.commons.rassist.test

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.container.ContainerEnable
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency

/**
 * @author GrowlyX
 * @since 5/8/2022
 */
@Plugin(
    name = "ReplayAssist-Test",
    authors = ["GrowlyX"],
    depends = [
        PluginDependency("scala-commons")
    ]
)
class ScalaReplayAssistPlugin : ExtendedScalaPlugin()
{
    @ContainerEnable
    fun containerEnable()
    {

    }
}
