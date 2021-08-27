package net.evilblock.cubed.util.bukkit.selection.impl

import net.evilblock.cubed.util.bukkit.selection.SelectionHandler
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

class InteractionHandler(private val metadataKey: String) : SelectionHandler<PlayerInteractEntityEvent>(metadataKey), Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (isAttached(event.player)) {
            (event.player.getMetadata(metadataKey)[0].value() as (Event) -> Unit).invoke(event)
        }
    }

}