package gg.scala.cloudsync

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.flavor.Flavor
import gg.scala.flavor.FlavorOptions
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
import xyz.mkotb.configapi.ConfigFactory

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
@Plugin(
    name = "cloudsync",
    description = "Scala's advanced server-to-cloud synchronization tool.",
    depends = [
        PluginDependency("Cubed")
    ]
)
class ScalaCloudSync : ExtendedScalaPlugin()
{
    private val flavor by lazy {
        Flavor.create<ScalaCloudSync>(
            FlavorOptions(logger)
        )
    }

    override fun enable()
    {
        flavor.bind<ScalaCloudSync>() to this
        flavor.bind<ConfigFactory>() to ConfigFactory.newFactory(this)
        flavor.startup()
    }

    override fun disable()
    {
        flavor.close()
    }
}
