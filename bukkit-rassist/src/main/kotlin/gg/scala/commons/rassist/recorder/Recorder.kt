package gg.scala.commons.rassist.recorder

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType as PEPacketType
import gg.scala.commons.rassist.RAssistBridge
import gg.scala.commons.rassist.dao.Replay
import gg.scala.commons.rassist.dao.ReplayMetadata
import gg.scala.commons.rassist.dao.ReplayTimestamp
import gg.scala.commons.rassist.dao.packet.ReplayPacketWrapper
import gg.scala.commons.rassist.dao.packet.enums.PacketAnimationType
import gg.scala.commons.rassist.dao.packet.enums.PacketEquipmentSlot
import gg.scala.commons.rassist.packet.ReplayPacketBridge
import gg.scala.commons.rassist.packet.PacketEventsReplayPacketFactory
import gg.scala.commons.rassist.util.ReplayPacketUtils
import me.lucko.helper.Events
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.ScalaCommonsSpigot
import org.bukkit.Bukkit
import org.bukkit.EntityEffect
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.*
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.logging.Level

class Recorder(
    private val uniqueId: UUID,
    private val players: List<UUID>
) : PacketListenerAbstract(PacketListenerPriority.MONITOR)
{
    private val packets =
        LinkedList<ReplayPacketWrapper>()

    private val metadata =
        ReplayMetadata(
            ReplayTimestamp(0L),
            this.players
        )

    private var status =
        RecorderActivityStatus.INACTIVE

    private fun active() =
        status == RecorderActivityStatus.ACTIVE

    private val terminable =
        CompositeTerminable.create()

    fun start()
    {
        val players = this.players
            .mapNotNull { Bukkit.getPlayer(it) }

        this.metadata.timestamps.beginning =
            System.currentTimeMillis()

        this.status = RecorderActivityStatus.ACTIVE

        for (recordedPlayer in players)
        {
            savePacket(
                PacketEventsReplayPacketFactory
                    .createLoginSuccessPacket(currentDelta(), recordedPlayer)
            )

            savePacket(
                PacketEventsReplayPacketFactory
                    .createLoginPacket(currentDelta(), recordedPlayer)
            )

            savePacket(
                PacketEventsReplayPacketFactory
                    .createPositionPacket(currentDelta(), recordedPlayer.location)
            )

            savePacket(
                PacketEventsReplayPacketFactory
                    .createPlayerSpawnPacket(currentDelta(), recordedPlayer)
            )

            savePacket(
                PacketEventsReplayPacketFactory
                    .createEntityEquipmentPacket(
                        currentDelta(),
                        recordedPlayer,
                        PacketEquipmentSlot.BOOTS,
                        recordedPlayer.equipment.boots
                    )
            )

            savePacket(
                PacketEventsReplayPacketFactory
                    .createEntityEquipmentPacket(
                        currentDelta(),
                        recordedPlayer,
                        PacketEquipmentSlot.LEGGINGS,
                        recordedPlayer.equipment.leggings
                    )
            )

            savePacket(
                PacketEventsReplayPacketFactory
                    .createEntityEquipmentPacket(
                        currentDelta(),
                        recordedPlayer,
                        PacketEquipmentSlot.CHESTPLATE,
                        recordedPlayer.equipment.chestplate
                    )
            )

            savePacket(
                PacketEventsReplayPacketFactory
                    .createEntityEquipmentPacket(
                        currentDelta(),
                        recordedPlayer,
                        PacketEquipmentSlot.HELMET,
                        recordedPlayer.equipment.helmet
                    )
            )

            savePacket(
                PacketEventsReplayPacketFactory
                    .createTeleportPacket(currentDelta(), recordedPlayer, recordedPlayer.location)
            )
        }
    }

    fun initialize()
    {
        RecorderStorage.recorders[uniqueId] = this
        PacketEvents.getAPI().eventManager.registerListener(this)

        Events.subscribe(PlayerRespawnEvent::class.java, EventPriority.MONITOR)
            .filter { active() && this.players.contains(it.player.uniqueId) }
            .handler {
                savePacket(
                    PacketEventsReplayPacketFactory
                        .createPlayerSpawnPacket(
                            currentDelta(),
                            it.player, it.respawnLocation
                        )
                )
            }
            .bindWith(terminable)

        Events.subscribe(EntityDeathEvent::class.java, EventPriority.MONITOR)
            .filter { active() && this.players.contains(it.entity.uniqueId) }
            .filter { it.entity is Player }
            .handler { event ->
                savePacket(
                    PacketEventsReplayPacketFactory
                        .createEntityDamagePacket(
                            currentDelta(),
                            event.entity as Player,
                            EntityEffect.DEATH
                        )
                )
            }
            .bindWith(terminable)

        Events.subscribe(EntityDamageEvent::class.java, EventPriority.MONITOR)
            .filter { active() && this.players.contains(it.entity.uniqueId) }
            .filter { it.entity is Player }
            .filter { (it.entity as Player).noDamageTicks <= ((it.entity as Player).maximumNoDamageTicks / 2.0) }
            .handler { event ->
                savePacket(
                    PacketEventsReplayPacketFactory
                        .createEntityDamagePacket(
                            currentDelta(),
                            event.entity as Player,
                            EntityEffect.HURT
                        )
                )
            }
            .bindWith(terminable)

        Events.subscribe(PlayerTeleportEvent::class.java, EventPriority.MONITOR)
            .filter { active() && this.players.contains(it.player.uniqueId) }
            .handler { event ->
                savePacket(
                    PacketEventsReplayPacketFactory
                        .createTeleportPacket(
                            currentDelta(),
                            event.player, event.to
                        )
                )
            }
            .bindWith(terminable)

        Events.subscribe(PlayerItemHeldEvent::class.java, EventPriority.MONITOR)
            .filter { active() && this.players.contains(it.player.uniqueId) }
            .handler { event ->
                savePacket(
                    PacketEventsReplayPacketFactory
                        .createEntityEquipmentPacket(
                            currentDelta(),
                            event.player,
                            PacketEquipmentSlot.HAND,
                            event.player.itemInHand
                        )
                )
            }
            .bindWith(terminable)

        Events.subscribe(PlayerAnimationEvent::class.java, EventPriority.MONITOR)
            .filter { active() && this.players.contains(it.player.uniqueId) }
            .handler { event ->
                savePacket(
                    PacketEventsReplayPacketFactory
                        .createEntityAnimationPacket(
                            currentDelta(),
                            event.player,
                            PacketAnimationType.SWING_ARM
                        )
                )
            }
            .bindWith(terminable)

        Events.subscribe(PlayerMoveEvent::class.java, EventPriority.MONITOR)
            .filter { active() && this.players.contains(it.player.uniqueId) }
            .handler { event ->
                if (event.from.yaw != event.to.yaw)
                {
                    savePacket(
                        PacketEventsReplayPacketFactory
                            .createHeadRotationPacket(
                                currentDelta(),
                                event.player, event.to.yaw
                            )
                    )
                }

                val move = event.to
                    .clone().subtract(event.from).toVector()

                if (ReplayPacketUtils.differentLocation(event.from, event.to))
                {
                    savePacket(
                        PacketEventsReplayPacketFactory
                            .createPositionLookPacket(
                                currentDelta(),
                                event.player, move,
                                event.to.yaw, event.to.pitch
                            )
                    )
                } else if (ReplayPacketUtils.hasRotated(event.from, event.to))
                {
                    savePacket(
                        PacketEventsReplayPacketFactory
                            .createLookPacket(
                                currentDelta(),
                                event.player, event.to.yaw, event.to.pitch
                            )
                    )
                } else
                {
                    savePacket(
                        PacketEventsReplayPacketFactory
                            .createPositionPacket(currentDelta(), event.player, move)
                    )
                }
            }
            .bindWith(terminable)
    }

    fun stopAndSave(): CompletableFuture<Replay>
    {
        this.metadata.timestamps.ending =
            System.currentTimeMillis()

        for (player in players)
        {
            val bukkit = Bukkit
                .getPlayer(player)
                ?: continue

            savePacket(
                PacketEventsReplayPacketFactory
                    .createDestroyPacket(currentDelta(),
                        bukkit
                    )
            )
        }

        val replay = Replay(
            this.metadata,
            this.packets
        )

        this.terminable
            .closeAndReportException()

        PacketEvents.getAPI().eventManager.unregisterListener(this)

        return CompletableFuture
            .supplyAsync {
                RAssistBridge.storage()
                    .persist(this.uniqueId, replay)

                replay
            }
            .exceptionally {
                ScalaCommonsSpigot.instance.logger.log(
                    Level.WARNING, "[Replay] We weren't able to save a replay", it
                )
                return@exceptionally null
            }
    }

    override fun onPacketReceive(event: PacketReceiveEvent)
    {
        val player = event.getPlayer<Player>() ?: return
        if (active() && this.players.contains(player.uniqueId))
        {
            if (event.packetType === PEPacketType.Play.Client.ANIMATION)
            {
                savePacket(
                    PacketEventsReplayPacketFactory
                        .convertAnimationPacket(currentDelta(), player)
                )
            }
        }
    }

    override fun onPacketSend(event: PacketSendEvent)
    {
        val player = event.getPlayer<Player>() ?: return
        if (active() && this.players.contains(player.uniqueId))
        {
            if (ReplayPacketBridge.packetTypesInternalPE.contains(event.packetType)) {
                savePacket(ReplayPacketWrapper.fromPacketEvent((System.currentTimeMillis() - this.metadata.timestamps.beginning).toInt(), event))
            }
        }
    }

    private fun savePacket(wrapper: ReplayPacketWrapper)
    {
        this.packets.add(wrapper)
    }

    private fun currentDelta(): Int = (System.currentTimeMillis() - this.metadata.timestamps.beginning).toInt()
}
