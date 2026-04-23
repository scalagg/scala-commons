package gg.scala.commons.sign

import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class SignGUI(private val plugin: Plugin)
{
    private val delegate by lazy {
        RaphaSignGUI(plugin as JavaPlugin)
    }

    fun open(player: Player, defaultText: Array<String>? = null, callback: (Player, Array<String>) -> Unit)
    {
        delegate.open(player, defaultText, callback)
    }

    fun destroy()
    {
    }
}
