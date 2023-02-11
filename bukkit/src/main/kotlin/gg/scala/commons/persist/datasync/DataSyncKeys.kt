package gg.scala.commons.persist.datasync

import net.kyori.adventure.key.Key

/**
 * @author GrowlyX
 * @since 2/10/2023
 */
interface DataSyncKeys
{
    fun store(): Key
    fun sync(): Key

    fun keyOf(namespace: String, value: String) =
        Key.key(namespace, value)
}
