/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.WrappedWatchableObject
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import me.lucko.helper.Events
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.config.CommonsGeneralConfig
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.event.PlayerDamageEntityEvent
import net.evilblock.cubed.entity.event.PlayerRightClickEntityEvent
import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.cubed.entity.menu.EditEntityMenu
import net.evilblock.cubed.entity.npc.protocol.NpcProtocol
import net.evilblock.cubed.entity.tool.EntityPortTool
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.ServerVersion
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
object EntityPacketAdapter : PacketAdapter(
    AdapterParameteters()
        .plugin(ScalaCommonsSpigot.instance)
        .types(
            PacketType.Play.Client.USE_ENTITY,
            if (ServerVersion.getVersion().isNewerThan(ServerVersion.v1_16))
            {
                PacketType.Play.Server.SPAWN_ENTITY
            } else
            {
                PacketType.Play.Server.SPAWN_ENTITY_LIVING
            }
        )
        .listenerPriority(ListenerPriority.NORMAL)
        .optionAsync()
)
{
    @Inject
    lateinit var injected: ExtendedScalaPlugin

    private val useEntitySpamTimings = ConcurrentHashMap<UUID, Long>()
    private val lastRightClick = ConcurrentHashMap<UUID, Long>()

    @Configure
    fun configure()
    {
        Events
            .subscribe(PlayerQuitEvent::class.java)
            .handler {
                useEntitySpamTimings.remove(it.player.uniqueId)
                lastRightClick.remove(it.player.uniqueId)
            }
            .bindWith(injected)

        val config = ScalaCommonsSpigot.instance
            .config<CommonsGeneralConfig>()

        if (ServerVersion.getVersion().isOlderThanOrEquals(ServerVersion.v1_8_8))
        {
            if (config.enableEntityPacketAdapters)
            {
                ProtocolLibrary.getProtocolManager()
                    .addPacketListener(this)
            }
        }
    }

    /**
     * Handles protocol changes for holograms.
     */
    override fun onPacketSending(event: PacketEvent)
    {
        if (!NpcProtocol.tropicSpigotEnabled)
        {
            if (event.packetType == PacketType.Play.Server.SPAWN_ENTITY_LIVING)
            {
                val entityId = event.packet.integers.read(0)
                val entity = EntityHandler.getEntityById(entityId) ?: return

                if (entity is HologramEntity)
                {
                    val dataWatcher = event.packet.dataWatcherModifier.read(0)
                    dataWatcher.watchableObjects.add(WrappedWatchableObject(12, -1700000))
                    event.packet.dataWatcherModifier.write(0, dataWatcher)
                }
            }
        }
    }

    override fun onPacketReceiving(event: PacketEvent)
    {
        if (event.packetType == PacketType.Play.Client.USE_ENTITY)
        {
            val entityId = event.packet.integers.read(0)

            val entity = EntityHandler.getEntityById(entityId) ?: return
            if (entity.location.world != event.player.world || entity.location.distanceSquared(event.player.location) > 8.0)
            {
                return
            }

            if (useEntitySpamTimings.containsKey(event.player.uniqueId))
            {
                val lastTime = useEntitySpamTimings[event.player.uniqueId]!!
                if (System.currentTimeMillis() - lastTime < 25)
                {
                    event.isCancelled = true
                    return
                }
            }

            useEntitySpamTimings[event.player.uniqueId] = System.currentTimeMillis()

            // ProtocolLib on 1.7 server doesn't recognize 1.8 client INTERACT_AT action, so the action will be null
            val useAction = Reflection.getDeclaredFieldValue(event.packet.handle, "action") as Enum<*>?
            when (useAction?.name)
            {
                null, INTERACT, INTERACT_AT ->
                {
                    val rightClickEvent = PlayerRightClickEntityEvent(event.player, entity)
                    Bukkit.getPluginManager().callEvent(rightClickEvent)

                    if (rightClickEvent.spamProtection)
                    {
                        if (lastRightClick.contains(event.player.uniqueId))
                        {
                            val lastRightClick = lastRightClick[event.player.uniqueId]!!

                            if (System.currentTimeMillis() < lastRightClick + 500)
                            {
                                return
                            }
                        }
                    }

                    lastRightClick[event.player.uniqueId] = System.currentTimeMillis()

                    if (!rightClickEvent.isCancelled)
                    {
                        // if export tool is enabled, mark/un-mark entity for export
                        if (event.player.hasPermission("commons.entity.export") && EntityPortTool.isTracking(event.player))
                        {
                            if (EntityPortTool.isTrackingEntity(event.player, entity))
                            {
                                EntityPortTool.removeTrackedEntity(event.player, entity)
                                event.player.sendMessage(
                                    "${ChatColor.YELLOW}Entity #${entity.id} is no longer marked for export! ${ChatColor.GRAY}(${
                                        EntityPortTool.getTrackedEntities(
                                            event.player
                                        ).size
                                    } selected)"
                                )
                            } else
                            {
                                EntityPortTool.addTrackedEntity(event.player, entity)
                                event.player.sendMessage(
                                    "${ChatColor.GREEN}Entity #${entity.id} is now marked for export! ${ChatColor.GRAY}(${
                                        EntityPortTool.getTrackedEntities(
                                            event.player
                                        ).size
                                    } selected)"
                                )
                            }

                            return
                        }

                        // automatically open entity management menu if player is in fast edit mode
                        if (event.player.gameMode == GameMode.CREATIVE && event.player.hasMetadata("EntityFastEdit"))
                        {
                            Tasks.delayed(1L) {
                                EditEntityMenu(entity).openMenu(event.player)
                            }

                            return
                        }

                        entity.onRightClick(event.player)
                    }
                }

                ATTACK ->
                {
                    entity.onLeftClick(event.player)

                    if (entity.isDamageable())
                    {
                        val damageEvent = PlayerDamageEntityEvent(event.player, entity)
                        Bukkit.getPluginManager().callEvent(damageEvent)

                        if (!damageEvent.isCancelled)
                        {
                            // work later
                        }
                    }
                }

                else ->
                {
                }
            }
        }
    }

    /**
     * Use constant string names in place of the EntityUseAction enum for compatibility.
     */
    private const val INTERACT = "INTERACT"
    private const val INTERACT_AT = "INTERACT_AT"
    private const val ATTACK = "ATTACK"

}
