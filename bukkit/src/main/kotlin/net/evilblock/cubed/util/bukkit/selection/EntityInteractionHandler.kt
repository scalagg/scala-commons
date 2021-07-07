package net.evilblock.cubed.util.bukkit.selection

import net.evilblock.cubed.Cubed
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.metadata.FixedMetadataValue

class EntityInteractionHandler(private val metadataKey: String) : Listener {

    fun isAttached(player: Player): Boolean {
        return player.hasMetadata(metadataKey)
    }

    fun attachHandler(player: Player, lambda: (PlayerInteractEntityEvent) -> Unit) {
        player.setMetadata(metadataKey, FixedMetadataValue(Cubed.instance, lambda))
    }

    fun removeHandler(player: Player) {
        player.removeMetadata(metadataKey, Cubed.instance)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerInteractEntityEvent(event: PlayerInteractEntityEvent) {
        if (isAttached(event.player)) {
            (event.player.getMetadata(metadataKey)[0].value() as (Event) -> Unit).invoke(event)
        }
    }

}