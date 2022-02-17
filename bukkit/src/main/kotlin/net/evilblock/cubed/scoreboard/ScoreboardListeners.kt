/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.scoreboard

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerJoinEvent

object ScoreboardListeners : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        ScoreboardHandler.scoreboards[event.player.uniqueId] =
            Scoreboard(event.player).also { it.update() }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        ScoreboardHandler.scoreboards.remove(event.player.uniqueId)
    }

}
