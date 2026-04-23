package gg.scala.commons.rassist.dao

import gg.scala.commons.rassist.dao.packet.ReplayPacketWrapper
import java.util.*

/**
 * @author GrowlyX
 * @since 5/7/2022
 */
data class Replay(
    val metadata: ReplayMetadata,
    val packets: LinkedList<ReplayPacketWrapper>
)
{
    fun getPacket(index: Int): ReplayPacketWrapper?
    {
        if (index >= 0 && index < packets.size)
        {
            return packets[index]
        }

        return null
    }
}
