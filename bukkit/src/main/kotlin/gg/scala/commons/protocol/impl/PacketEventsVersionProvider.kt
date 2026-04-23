package gg.scala.commons.protocol.impl

import com.github.retrooper.packetevents.PacketEvents
import gg.scala.commons.protocol.ProtocolVersion
import org.bukkit.entity.Player

object PacketEventsVersionProvider : ProtocolVersion
{
    override fun getProtocolVersion(player: Player): Int =
        PacketEvents.getAPI().playerManager.getClientVersion(player).protocolVersion
}
