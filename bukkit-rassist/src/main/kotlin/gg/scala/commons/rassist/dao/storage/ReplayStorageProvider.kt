package gg.scala.commons.rassist.dao.storage

import gg.scala.commons.rassist.dao.Replay
import java.util.UUID

interface ReplayStorageProvider
{
    fun persist(uniqueId: UUID, replay: Replay)
    fun retrieve(uniqueId: UUID): Replay?

    fun all(): List<Replay>
}
