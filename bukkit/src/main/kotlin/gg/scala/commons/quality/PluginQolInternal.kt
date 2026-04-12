package gg.scala.commons.quality

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.quality.contract.QualityOfLife
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants

class PluginQolInternal(
    val plugin: ExtendedScalaPlugin
)
{
    lateinit var prefix: String

    fun load()
    {
        val qualityOfLife = plugin
            .contract<QualityOfLife>()

        val pluginName = plugin.name

        val formatted = "${
            qualityOfLife?.representingColor()
                ?: CC.PRI
        }${CC.BOLD}$pluginName ${CC.GRAY}${
            Constants.DOUBLE_ARROW_RIGHT
        }"

        this.prefix = formatted
    }
}
