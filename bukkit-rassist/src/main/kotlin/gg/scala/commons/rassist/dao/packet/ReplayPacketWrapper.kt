package gg.scala.commons.rassist.dao.packet

import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.injector.netty.WirePacket
import gg.scala.commons.rassist.packet.ReplayPacketBridge

/**
 * @author GrowlyX
 * @since 5/7/2022
 */
class ReplayPacketWrapper(
    val time: Int,
    id: Int, data: ByteArray
) : WirePacket(id, data)
{
    constructor(
        time: Int,
        container: PacketContainer
    ) : this(
        time,
        ReplayPacketBridge.converter()
            .getPacketID(container.type),
        bytesFromPacket(container)
    )
}
