package gg.scala.commons.rassist.dao.storage

import gg.scala.commons.rassist.dao.Replay
import java.util.UUID

/**
 * @author GrowlyX
 * @since 5/7/2022
 */
interface ReplayStorageProvider
{
    fun persist(uniqueId: UUID, replay: Replay)
}
