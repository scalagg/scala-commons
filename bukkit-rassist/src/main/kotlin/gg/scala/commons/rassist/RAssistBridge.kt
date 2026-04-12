package gg.scala.commons.rassist

import gg.scala.commons.rassist.dao.storage.ReplayStorageProvider
import gg.scala.commons.rassist.dao.storage.impl.LocalReplayStorageProvider

object RAssistBridge
{
    private lateinit var storageProvider: ReplayStorageProvider

    fun storage() = this.storageProvider

    init
    {
        this.useStorageProvider(
            LocalReplayStorageProvider
        )
    }

    fun useStorageProvider(
        storageProvider: ReplayStorageProvider
    )
    {
        this.storageProvider = storageProvider
    }
}
