/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.nametag.update

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scoreboard.DisplaySlot

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