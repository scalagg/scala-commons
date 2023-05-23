package gg.scala.commons.exampleplugin.spigot

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.core.plugin.Plugin
import gg.scala.commons.core.plugin.PluginAuthor
import gg.scala.commons.core.plugin.PluginDependency
import gg.scala.commons.core.plugin.PluginWebsite

/**
 * @author GrowlyX
 * @since 10/10/2022
 */
@Plugin(
    name = "devtoolkit",
    version = "%remote%/%branch%/%id%"
)
@PluginAuthor("%author%")
@PluginWebsite("%website%")
@PluginDependency("scala-commons")
class ExamplePlugin : ExtendedScalaPlugin()
