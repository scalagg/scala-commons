package net.evilblock.cubed.entity.tool

import gg.scala.commons.annotations.Listeners
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.event.PlayerRightClickEntityEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.metadata.FixedMetadataValue

@Listeners
object EntitySelectionTool : Listener {

    @JvmStatic
    fun isSelecting(player: Player): Boolean {
        return player.hasMetadata("ENTITY_SELECTION")
    }

    @JvmStatic
    fun attachSelectionHandler(player: Player, lambda: (Entity) -> Unit) {
        player.setMetadata("ENTITY_SELECTION", FixedMetadataValue(Cubed.instance, lambda))
    }

    @JvmStatic
    fun handleSelection(player: Player, entity: Entity) {
        // first get the callback func
        val callbackFunction = player.getMetadata("ENTITY_SELECTION")[0].value() as (Entity) -> Unit

        // then remove the metadata, that way we can assign another entity selection
        // handler from within our callback func
        player.removeMetadata("ENTITY_SELECTION", Cubed.instance)

        // invoke our callback func
        callbackFunction.invoke(entity)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerRightClickEntityEvent(event: PlayerRightClickEntityEvent) {
        if (isSelecting(event.player)) {
            event.isCancelled = true

            handleSelection(event.player, event.entity)
        }
    }

}
