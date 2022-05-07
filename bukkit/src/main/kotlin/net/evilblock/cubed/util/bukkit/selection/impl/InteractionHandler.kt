package net.evilblock.cubed.util.bukkit.selection.impl

import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.util.bukkit.selection.SelectionHandler
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

class InteractionHandler(
    private val metadataKey: String,
    plugin: ScalaCommonsSpigot
) : SelectionHandler<PlayerInteractEntityEvent>(metadataKey, plugin) {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (isAttached(event.player)) {
            (event.player.getMetadata(metadataKey)[0].value() as (Event) -> Unit).invoke(event)
        }
    }

}
