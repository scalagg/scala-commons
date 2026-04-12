

package net.evilblock.cubed.nametag.update

import gg.scala.commons.annotations.Listeners
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scoreboard.DisplaySlot

@Listeners
object NametagUpdatesListeners : Listener {

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val scoreboard = event.player.scoreboard
        if (scoreboard != null) {
            for (team in scoreboard.teams) {
                val entries = team.entries
                for (entry in entries) {
                    team.removeEntry(entry)
                }

                team.unregister()
            }

            scoreboard.clearSlot(DisplaySlot.PLAYER_LIST)
        }
    }

}
