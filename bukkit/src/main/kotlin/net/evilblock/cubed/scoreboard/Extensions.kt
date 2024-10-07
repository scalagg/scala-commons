package net.evilblock.cubed.scoreboard

import com.comphenix.protocol.events.PacketContainer
import me.lucko.helper.protocol.Protocol
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 10/7/2024
 */
fun PacketContainer.send(player: Player)
{
    Protocol.sendPacket(player, this)
}
