/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.WrappedWatchableObject
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.event.PlayerDamageEntityEvent
import net.evilblock.cubed.entity.event.PlayerRightClickEntityEvent
import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.cubed.entity.menu.EditEntityMenu
import net.evilblock.cubed.entity.tool.EntityPortTool
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object EntityPacketAdapter : PacketAdapter(
    Cubed.instance,
    PacketType.Play.Client.USE_ENTITY,
    PacketType.Play.Server.SPAWN_ENTITY_LIVING
), Listener {

    private val useEntitySpamTimings = ConcurrentHashMap<UUID, Long>()
    private val lastRightClick = ConcurrentHashMap<UUID, Long>()

    override fun onPacketReceiving(event: PacketEvent) {
        if (event.packetType == PacketType.Play.Client.USE_ENTITY) {
            val entityId = event.packet.integers.read(0)

            val entity = EntityHandler.getEntityById(entityId) ?: return
            if (entity.location.world != event.player.world || entity.location.distanceSquared(event.player.location) > 8.0) {
                return
            }

            if (useEntitySpamTimings.containsKey(event.player.uniqueId)) {
                val lastTime = useEntitySpamTimings[event.player.uniqueId]!!
                if (System.currentTimeMillis() - lastTime < 25) {
                    event.isCancelled = true
                    return
                }
            }

            useEntitySpamTimings[event.player.uniqueId] = System.currentTimeMillis()

            // ProtocolLib on 1.7 server doesn't recognize 1.8 client INTERACT_AT action, so the action will be null
            val useAction = Reflection.getDeclaredFieldValue(event.packet.handle, "action") as Enum<*>?
            when (useAction?.name) {
                null, INTERACT, INTERACT_AT -> {
                    val rightClickEvent = PlayerRightClickEntityEvent(event.player, entity)
                    Bukkit.getPluginManager().callEvent(rightClickEvent)

                    if (rightClickEvent.spamProtection) {
                        if (lastRightClick.contains(event.player.uniqueId)) {
                            val lastRightClick = lastRightClick[event.player.uniqueId]!!
                            if (System.currentTimeMillis() < lastRightClick + 500) {
                                return
                            }
                        }
                    }

                    lastRightClick[event.player.uniqueId] = System.currentTimeMillis()

                    if (!rightClickEvent.isCancelled) {
                        // if export tool is enabled, mark/un-mark entity for export
                        if (event.player.hasPermission("cubed.entity.export") && EntityPortTool.isTracking(event.player)) {
                            if (EntityPortTool.isTrackingEntity(event.player, entity)) {
                                EntityPortTool.removeTrackedEntity(event.player, entity)
                                event.player.sendMessage("${ChatColor.YELLOW}Entity #${entity.id} is no longer marked for export! ${ChatColor.GRAY}(${EntityPortTool.getTrackedEntities(event.player).size} selected)")
                            } else {
                                EntityPortTool.addTrackedEntity(event.player, entity)
                                event.player.sendMessage("${ChatColor.GREEN}Entity #${entity.id} is now marked for export! ${ChatColor.GRAY}(${EntityPortTool.getTrackedEntities(event.player).size} selected)")
                            }

                            return
                        }

                        // automatically open entity management menu if player is in fast edit mode
                        if (event.player.gameMode == GameMode.CREATIVE && event.player.hasMetadata("EntityFastEdit")) {
                            Tasks.delayed(1L) {
                                EditEntityMenu(entity).openMenu(event.player)
                            }

                            return
                        }

                        entity.onRightClick(event.player)
                    }
                }
                ATTACK -> {
                    entity.onLeftClick(event.player)

                    if (entity.isDamageable()) {
                        val damageEvent = PlayerDamageEntityEvent(event.player, entity)
                        Bukkit.getPluginManager().callEvent(damageEvent)

                        if (!damageEvent.isCancelled) {
                            // work later
                        }
                    }
                }
                else -> {}
            }
        }
    }

    /**
     * Handles protocol changes for holograms.
     */
    override fun onPacketSending(event: PacketEvent) {
        if (event.packetType == PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
            val entityId = event.packet.integers.read(0)
            val entity = EntityHandler.getEntityById(entityId) ?: return
            if (entity is HologramEntity) {
                val dataWatcher = event.packet.dataWatcherModifier.read(0)
                dataWatcher.watchableObjects.add(WrappedWatchableObject(12, -1700000))
                event.packet.dataWatcherModifier.write(0, dataWatcher)
            }
        }

//        if (event.packetType == PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
//            val entityId = event.packet.integers.read(0)
//            println("spawning named entity $entityId for ${event.player.name}")
//            val dataWatcher = event.packet.dataWatcherModifier.read(0)
//            for ((key, value) in dataWatcher.asMap()) {
//                println("$key - ${value.value}")
//            }
//        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        useEntitySpamTimings.remove(event.player.uniqueId)
        lastRightClick.remove(event.player.uniqueId)
    }

    /**
     * Use constant string names in place of the EntityUseAction enum for compatibility.
     */
    private const val INTERACT = "INTERACT"
    private const val INTERACT_AT = "INTERACT_AT"
    private const val ATTACK = "ATTACK"

}