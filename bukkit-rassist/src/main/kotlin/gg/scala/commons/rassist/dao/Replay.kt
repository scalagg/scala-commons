package gg.scala.commons.rassist.dao

import gg.scala.commons.rassist.dao.packet.ReplayPacketWrapper
import java.util.*

/**
 * @author GrowlyX
 * @since 5/7/2022
 */
data class Replay(
    val metadata: ReplayMetadata,
    val packets: Map<UUID, LinkedList<ReplayPacketWrapper>>
)
{
    fun getPacket(index: Int): List<ReplayPacketWrapper>
    {
        val packets =
            mutableListOf<ReplayPacketWrapper>()

        for (key in this.packets.keys)
        {
            val list = this.packets[key]!!

            if (index >= 0 && index < list.size)
            {
                packets.add(list[index])
            }
        }

        return packets
    }
}
