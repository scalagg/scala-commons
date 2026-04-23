package net.evilblock.cubed.scoreboard

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 10/7/2024
 */
fun PacketWrapper<*>.send(player: Player)
{
    PacketEvents.getAPI().playerManager.getUser(player)
        .apply {
            sendPacket(this@send)
        }
}
