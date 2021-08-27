package net.evilblock.cubed.plugin.event

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

object EventListener: Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.hasItem())
            return

        val interactItemEvent = PlayerInteractItemEvent(event.player, event.item, Action.valueOf(event.action.name))

        if (interactItemEvent.call()) {
            event.isCancelled = true
        }
    }
}
