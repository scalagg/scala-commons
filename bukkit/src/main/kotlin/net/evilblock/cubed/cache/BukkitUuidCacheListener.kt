package net.evilblock.cubed.cache

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

/**
 * @author GrowlyX
 * @since 2/1/2022
 */
object BukkitUuidCacheListener : Listener
{
    @EventHandler
    fun onAsyncPreLogin(event: AsyncPlayerPreLoginEvent)
    {

    }
}
