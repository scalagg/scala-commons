package gg.scala.commons.protocol.impl

import com.comphenix.protocol.ProtocolLibrary
import gg.scala.commons.protocol.ProtocolVersion
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/14/2022
 */
object ProtocolLibVersionProvider : ProtocolVersion
{
    override fun getProtocolVersion(player: Player) =
        ProtocolLibrary.getProtocolManager().getProtocolVersion(player)
}
