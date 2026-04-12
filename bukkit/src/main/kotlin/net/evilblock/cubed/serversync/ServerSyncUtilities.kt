package net.evilblock.cubed.serversync

import org.bukkit.entity.Player

object ServerSyncUtilities
{
    var filterPlayerCanReceiveNotifications = { _: Player -> true }
}
