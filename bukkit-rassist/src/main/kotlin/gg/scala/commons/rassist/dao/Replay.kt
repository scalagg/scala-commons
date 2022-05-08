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
