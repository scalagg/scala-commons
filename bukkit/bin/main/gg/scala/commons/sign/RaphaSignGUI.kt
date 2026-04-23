package gg.scala.commons.sign

import de.rapha149.signgui.SignGUI as RaphaSign
import de.rapha149.signgui.SignGUIAction
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * Wrapper around Rapha149/SignGUI library for sign input.
 *
 * @author Limo
 * @date 2/21/2026
 */
class RaphaSignGUI(private val plugin: JavaPlugin)
{
    fun open(player: Player, defaultText: Array<String>? = null, callback: (Player, Array<String>) -> Unit)
    {
        val builder = RaphaSign.builder()

        if (defaultText != null)
        {
            for (i in defaultText.indices)
            {
                builder.setLine(i, defaultText[i])
            }
        }

        builder
            .setHandler { p, result ->
                callback(p, result.lines)
                listOf(SignGUIAction.run { })
            }
            .callHandlerSynchronously(plugin)
            .build()
            .open(player)
    }
}
