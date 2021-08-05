/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.store.uuidcache.listener

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.store.uuidcache.event.UUIDCacheUpdateEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

object UUIDCacheListeners : Listener {

    @EventHandler
    fun onAsyncPlayerPreLoginEvent(event: AsyncPlayerPreLoginEvent) {
        var update = false
        if (Cubed.instance.uuidCache.isCached(event.uniqueId)) {
            val name = Cubed.instance.uuidCache.name(event.uniqueId)
            if (name != event.name) {
                update = true
            }
        } else {
            update = true
        }

        if (update) {
            Cubed.instance.uuidCache.update(event.uniqueId, event.name, commit = true)
            UUIDCacheUpdateEvent(event.uniqueId, event.name).call()
        }
    }
}