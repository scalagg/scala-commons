package net.evilblock.cubed.util.bukkit.selection

import net.evilblock.cubed.ScalaCommonsSpigot
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.metadata.FixedMetadataValue

abstract class SelectionHandler<Event>(
    private val metadataKey: String,
    private val plugin: ScalaCommonsSpigot
) : Listener {

    init {
        plugin.server.pluginManager.registerEvents((this as Listener), plugin)
    }

    fun isAttached(player: Player): Boolean {
        return player.hasMetadata(metadataKey)
    }

    fun attachHandler(player: Player, lambda: (Event) -> Unit) {
        player.setMetadata(metadataKey, FixedMetadataValue(plugin, lambda))
    }

    fun removeHandler(player: Player) {
        player.removeMetadata(metadataKey, plugin)
    }
}
