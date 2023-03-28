package net.evilblock.cubed.serversync

import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 3/28/2023
 */
object ServerSyncUtilities
{
    var filterPlayerCanReceiveNotifications = { _: Player -> true }
}
