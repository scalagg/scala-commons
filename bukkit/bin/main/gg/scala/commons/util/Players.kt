package gg.scala.commons.util

import org.bukkit.Bukkit

/**
 * @author GrowlyX
 * @since 5/2/2022
 */
object Players
{
    @JvmStatic
    fun operators() = Bukkit
        .getOnlinePlayers()
        .filter { it.isOp }
}
