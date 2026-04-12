package net.evilblock.cubed.scoreboard

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import org.bukkit.entity.Player

fun PacketWrapper<*>.send(player: Player)
{
    PacketEvents.getAPI().playerManager.getUser(player)
        ?.sendPacket(this@send)
}
