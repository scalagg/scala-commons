package gg.scala.commons.rassist.dao.storage.impl

import gg.scala.commons.rassist.dao.Replay
import gg.scala.commons.rassist.dao.storage.ReplayStorageProvider
import java.util.*

object LocalReplayStorageProvider : ReplayStorageProvider
{
    private val localStorage =
        mutableMapOf<UUID, Replay>()

    override fun persist(
        uniqueId: UUID, replay: Replay
    )
    {
        localStorage[uniqueId] = replay
    }

    override fun retrieve(uniqueId: UUID) =
        localStorage[uniqueId]

    override fun all() = this
        .localStorage.values.toList()
}
