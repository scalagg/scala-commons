package net.evilblock.cubed.util.bukkit.selection

import net.evilblock.cubed.Cubed
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.metadata.FixedMetadataValue

abstract class SelectionHandler<Event>(private val metadataKey: String) : Listener {

    init {
        Cubed.instance.server.pluginManager.registerEvents((this as Listener), Cubed.instance)
    }

    fun isAttached(player: Player): Boolean {
        return player.hasMetadata(metadataKey)
    }

    fun attachHandler(player: Player, lambda: (Event) -> Unit) {
        player.setMetadata(metadataKey, FixedMetadataValue(Cubed.instance, lambda))
    }

    fun removeHandler(player: Player) {
        player.removeMetadata(metadataKey, Cubed.instance)
    }
}