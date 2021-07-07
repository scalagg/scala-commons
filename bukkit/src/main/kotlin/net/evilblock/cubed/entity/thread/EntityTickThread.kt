package net.evilblock.cubed.entity.thread

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.cubed.entity.tick.TickableEntity
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.util.math.Numbers
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode

object EntityTickThread : Thread("Cubed - Entities") {

    var DEBUG = false

    var thrownExceptions: Int = 0
    var lastAlert: Long? = null

    var ticks: Long = 0L

    override fun run() {
        while (true) {
            try {
                tickEntities()

                if (ticks++ % 10 == 0L) {
                    updateWatchers()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            sleep(50L)
        }
    }

    private fun tickEntities() {
        if (lastAlert == null || System.currentTimeMillis() >= lastAlert!! + 60_000L) {
            if (thrownExceptions > 0) {
                PluginFramework.systemLog(messages = *arrayOf("${ChatColor.YELLOW}[Entity Ticking] ${ChatColor.GRAY}There have been ${ChatColor.RED}${Numbers.format(thrownExceptions)} ${ChatColor.GRAY}thrown exceptions in the last 60 seconds!"))
            }

            thrownExceptions = 0
            lastAlert = System.currentTimeMillis()
        }

        for (entity in EntityHandler.getEntities()) {
            try {
                if (entity is TickableEntity) {
                    entity.tick()
                }

                if (entity.animation != null) {
                    val animation = entity.animation!!
                    if (animation.lastTick == null || System.currentTimeMillis() - animation.lastTick!! >= animation.getInterval()) {
                        animation.current++
                        animation.lastTick = System.currentTimeMillis()
                        animation.tick()
                    }
                }
            } catch (e: Exception) {
                if (DEBUG) {
                    e.printStackTrace()
                } else {
                    thrownExceptions++
                }
            }
        }
    }

    private fun updateWatchers() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!player.isOnline) {
                continue
            }

            val showDebug = (player.isOp || player.hasPermission("cubed.entities.debug"))
                    && player.gameMode == GameMode.CREATIVE
                    && EntityHandler.isDebugViewEnabled(player)

            for (entity in EntityHandler.getEntities()) {
                try {
                    val isVisible = entity.isVisibleToPlayer(player)
                    val currentlyWatching = entity.isWatcher(player.uniqueId)

                    if (isVisible && !currentlyWatching) {
                        entity.spawn(player)
                    } else if (!isVisible && currentlyWatching) {
                        entity.destroy(player)
                    }
                } catch (e: Exception) {
                    Cubed.instance.logger.warning("Failed to update entity ${entity.uuid} for player ${player.name}")
                    e.printStackTrace()
                }

                if (entity.isWatcher(player.uniqueId)) {
                    if (showDebug && entity.canDisplayDebug()) {
                        if (!entity.debugView.containsKey(player.uniqueId)) {
                            try {
                                val debugHologram = HologramEntity(text = "", location = entity.getDebugViewLocation())
                                debugHologram.initializeData()
                                debugHologram.updateLines(entity.renderDebugInformation())

                                val attachedHologram = entity.getAttachedHologram()
                                if (attachedHologram != null) {
                                    debugHologram.offset = attachedHologram.offset
                                }

                                debugHologram.spawn(player)

                                entity.debugView[player.uniqueId] = debugHologram
                            } catch (e: Exception) {
                                Cubed.instance.logger.warning("Failed to update debug entity ${entity.uuid} for player ${player.name}")
                                e.printStackTrace()
                            }
                        }
                    } else {
                        if (entity.debugView.containsKey(player.uniqueId)) {
                            entity.debugView.remove(player.uniqueId)!!.destroy(player)
                        }
                    }
                }
            }

            if (EntityHandler.isTrackingInstanceEntities(player)) {
                for (entity in EntityHandler.getInstanceEntities(player)) {
                    try {
                        val isVisible = entity.isVisibleToPlayer(player)
                        val currentlyWatching = entity.isWatcher(player.uniqueId)

                        if (isVisible && !currentlyWatching) {
                            entity.spawn(player)
                        } else if (!isVisible && currentlyWatching) {
                            entity.destroy(player)
                        }
                    } catch (e: Exception) {
                        Cubed.instance.logger.warning("Failed to update instance entity ${entity.uuid} for player ${player.name}")
                        e.printStackTrace()
                    }
                }
            }
        }
    }

}