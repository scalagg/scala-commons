package gg.scala.commons.downloader

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
    depends = [
        PluginDependency("Cubed")
    ]
)
class ScalaCommonsDownloader : ExtendedScalaPlugin()
{
    val flavor by lazy {
        Flavor.create<ScalaCommonsDownloader>(
            FlavorOptions(logger)
        )
    }

    override fun enable()
    {
        flavor.bind<ScalaCommonsDownloader>() to this
        flavor.bind<ConfigFactory>() to ConfigFactory.newFactory(this)
        flavor.startup()
    }

    override fun disable()
    {
        flavor.close()
    }
}
