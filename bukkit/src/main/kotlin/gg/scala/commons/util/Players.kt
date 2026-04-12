package gg.scala.commons.util

import org.bukkit.Bukkit

object Players
{
    @JvmStatic
    fun operators() = Bukkit
        .getOnlinePlayers()
        .filter { it.isOp }
}
