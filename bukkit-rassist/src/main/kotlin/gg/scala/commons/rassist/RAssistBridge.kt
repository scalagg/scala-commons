package gg.scala.commons.rassist

import gg.scala.commons.rassist.dao.storage.ReplayStorageProvider

/**
 * @author GrowlyX
 * @since 5/7/2022
 */
object RAssistBridge
{
    private lateinit var storageProvider: ReplayStorageProvider

    fun storage() = this.storageProvider

    fun useStorageProvider(
        storageProvider: ReplayStorageProvider
    )
    {
        this.storageProvider = storageProvider
    }
}
