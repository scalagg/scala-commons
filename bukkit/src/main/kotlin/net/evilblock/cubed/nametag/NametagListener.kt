package net.evilblock.cubed.nametag

import gg.scala.commons.annotations.Listeners
import net.evilblock.cubed.Cubed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import org.bukkit.event.player.PlayerJoinEvent

@Listeners
object NametagListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.setMetadata("Nametag-Applied", FixedMetadataValue(Cubed.instance, true) as MetadataValue)
        NametagHandler.initiatePlayer(event.player)
        NametagHandler.reloadPlayer(event.player)
        NametagHandler.reloadOthersFor(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.player.removeMetadata("Nametag-Applied", Cubed.instance)
        NametagHandler.teamMap.remove(event.player.uniqueId)
    }

}
