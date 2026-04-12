package gg.scala.commons.protocol

import org.bukkit.entity.Player

interface ProtocolVersion
{
    fun getProtocolVersion(player: Player): Int
}
