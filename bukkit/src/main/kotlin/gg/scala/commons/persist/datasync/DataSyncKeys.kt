package gg.scala.commons.persist.datasync

import net.kyori.adventure.key.Key

interface DataSyncKeys
{
    fun store(): Key

    fun newStore(): String
    {
        return store().asString()
    }
    fun sync(): Key

    fun keyOf(namespace: String, value: String) =
        Key.key(namespace, value)
}
