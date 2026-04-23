package net.evilblock.cubed.entity.packet

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import me.lucko.helper.Events
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.config.CommonsGeneralConfig
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.event.PlayerDamageEntityEvent
import net.evilblock.cubed.entity.event.PlayerRightClickEntityEvent
import net.evilblock.cubed.entity.menu.EditEntityMenu
import net.evilblock.cubed.entity.npc.protocol.NpcProtocol
import net.evilblock.cubed.entity.tool.EntityPortTool
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
object EntityPacketAdapter : PacketListenerAbstract()
{
    @Inject
    lateinit var injected: ExtendedScalaPlugin

    private val useEntitySpamTimings = ConcurrentHashMap<UUID, Long>()
    private val lastRightClick = ConcurrentHashMap<UUID, Long>()
    private var registered = false

    @Configure
    fun configure()
    {
        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                useEntitySpamTimings.remove(it.player.uniqueId)
                lastRightClick.remove(it.player.uniqueId)
            }
            .bindWith(injected)

        val config = ScalaCommonsSpigot.instance.config<CommonsGeneralConfig>()
        if (config.enableEntityPacketAdapters && !registered)
        {
            PacketEvents.getAPI().eventManager.registerListener(this)
            registered = true
        }
    }

    override fun onPacketReceive(event: PacketReceiveEvent)
    {
        if (event.packetType != PacketType.Play.Client.INTERACT_ENTITY) {
            return
        }

        val player = event.getPlayer<org.bukkit.entity.Player>() ?: return
        if (!Bukkit.isPrimaryThread())
        {
            val cloned = event.clone()
            Bukkit.getScheduler().runTask(ScalaCommonsSpigot.instance) {
                handleUseEntity(player, WrapperPlayClientInteractEntity(cloned))
            }
            return
        }

        handleUseEntity(player, WrapperPlayClientInteractEntity(event))
    }

    private fun handleUseEntity(player: org.bukkit.entity.Player, packet: WrapperPlayClientInteractEntity)
    {
        kotlin.runCatching {
            val entityId = packet.entityId
            val entity = findEntityById(entityId) ?: return
            if (entity.location.world != player.world || entity.location.distanceSquared(player.location) > 64.0) {
                return
            }

            val lastUseTime = useEntitySpamTimings[player.uniqueId]
            if (lastUseTime != null && System.currentTimeMillis() - lastUseTime < 25) {
                return
            }
            useEntitySpamTimings[player.uniqueId] = System.currentTimeMillis()

            when (packet.action) {
                WrapperPlayClientInteractEntity.InteractAction.INTERACT,
                WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT -> {
                    val rightClickEvent = PlayerRightClickEntityEvent(player, entity)
                    Bukkit.getPluginManager().callEvent(rightClickEvent)

                    if (rightClickEvent.spamProtection) {
                        val lastClick = lastRightClick[player.uniqueId]
                        if (lastClick != null && System.currentTimeMillis() < lastClick + 500) {
                            return
                        }
                    }

                    lastRightClick[player.uniqueId] = System.currentTimeMillis()

                    if (!rightClickEvent.isCancelled) {
                        if (player.hasPermission("commons.entity.export") && EntityPortTool.isTracking(player)) {
                            if (EntityPortTool.isTrackingEntity(player, entity)) {
                                EntityPortTool.removeTrackedEntity(player, entity)
                                player.sendMessage("${ChatColor.YELLOW}Entity #${entity.id} is no longer marked for export! ${ChatColor.GRAY}(${EntityPortTool.getTrackedEntities(player).size} selected)")
                            } else {
                                EntityPortTool.addTrackedEntity(player, entity)
                                player.sendMessage("${ChatColor.GREEN}Entity #${entity.id} is now marked for export! ${ChatColor.GRAY}(${EntityPortTool.getTrackedEntities(player).size} selected)")
                            }
                            return
                        }

                        if (player.gameMode == GameMode.CREATIVE && player.hasMetadata("EntityFastEdit")) {
                            Tasks.delayed(1L) {
                                EditEntityMenu(entity).openMenu(player)
                            }
                            return
                        }

                        entity.onRightClick(player)
                    }
                }

                WrapperPlayClientInteractEntity.InteractAction.ATTACK -> {
                    entity.onLeftClick(player)

                    if (entity.isDamageable()) {
                        val damageEvent = PlayerDamageEntityEvent(player, entity)
                        Bukkit.getPluginManager().callEvent(damageEvent)
                    }
                }
            }
        }.onFailure {
            ScalaCommonsSpigot.instance.logger.warning("Failed to handle use-entity packet: ${it.message}")
            it.printStackTrace()
        }
    }

    private fun findEntityById(entityId: Int): Entity?
    {
        return EntityHandler.getEntityById(entityId)
    }
}
