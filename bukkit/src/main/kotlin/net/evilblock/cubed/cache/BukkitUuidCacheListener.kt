package net.evilblock.cubed.cache

import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.commons.annotations.Listeners
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

@Listeners
object BukkitUuidCacheListener : Listener
{
    @EventHandler
    fun onAsyncPreLogin(event: AsyncPlayerPreLoginEvent)
    {
        val uniqueIdUsernamePair =
            Pair(event.uniqueId, event.name)

        ScalaStoreUuidCache.useTranslator {
            update(uniqueIdUsernamePair)
        }
    }
}
