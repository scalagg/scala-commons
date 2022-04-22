package gg.scala.commons.annotations.plugin.dependency

import gg.scala.commons.ExtendedScalaPlugin

/**
 * @author GrowlyX
 * @since 4/22/2022
 */
interface DependantFeature
{
    fun enable(plugin: ExtendedScalaPlugin)
    fun validate(): Boolean
}
