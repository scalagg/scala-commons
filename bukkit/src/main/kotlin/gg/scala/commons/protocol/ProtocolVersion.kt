package gg.scala.commons.protocol

import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/14/2022
 */
interface ProtocolVersion
{
    fun getProtocolVersion(player: Player): Int
}
