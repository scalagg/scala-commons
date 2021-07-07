/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.listener

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.*

object EntityWatcherListeners : Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        EntityHandler.startTrackingInstanceEntities(event.player)
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        for (entity in EntityHandler.stopTrackingInstanceEntities(event.player)) {
            entity.destroy(event.player)
        }

        Tasks.async {
            for (entity in EntityHandler.getEntities()) {
                try {
                    if (entity.isWatcher(event.player.uniqueId)) {
                        entity.destroy(event.player)
                    }
                } catch (e: Exception) {
                    Cubed.instance.logger.warning("Failed to update entity ${entity.uuid} for player ${event.player.name}")
                    e.printStackTrace()
                }
            }
        }
    }

    @EventHandler
    fun onPlayerChangedWorldEvent(event: PlayerChangedWorldEvent) {
        Tasks.asyncDelayed(20L) {
            for (entity in EntityHandler.getEntities()) {
                try {
                    if (entity.isVisibleToPlayer(event.player)) {
                        entity.spawn(event.player)
                    }
                } catch (e: Exception) {
                    Cubed.instance.logger.warning("Failed to update entity ${entity.uuid} for player ${event.player.name}")
                    e.printStackTrace()
                }
            }
        }
    }

    @EventHandler
    fun onPlayerRespawnEvent(event: PlayerRespawnEvent) {
        Tasks.asyncDelayed(10L) {
            for (entity in EntityHandler.getEntities()) {
                if (entity.isWatcher(event.player.uniqueId)) {
                    entity.destroy(event.player)

                    if (entity.isVisibleToPlayer(event.player)) {
                        entity.spawn(event.player)
                    }
                }
            }
        }
    }

}