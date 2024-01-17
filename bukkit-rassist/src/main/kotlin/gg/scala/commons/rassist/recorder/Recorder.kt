package gg.scala.commons.rassist.recorder

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import gg.scala.commons.rassist.RAssistBridge
import gg.scala.commons.rassist.dao.Replay
import gg.scala.commons.rassist.dao.ReplayMetadata
import gg.scala.commons.rassist.dao.ReplayTimestamp
import gg.scala.commons.rassist.dao.packet.ReplayPacketWrapper
import gg.scala.commons.rassist.dao.packet.enums.PacketAnimationType
import gg.scala.commons.rassist.dao.packet.enums.PacketEquipmentSlot
import gg.scala.commons.rassist.packet.ReplayPacketBridge
import gg.scala.commons.rassist.util.ReplayPacketUtils
import me.lucko.helper.Events
import me.lucko.helper.protocol.Protocol
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

/**
 * @author GrowlyX
 * @since 5/7/2022
 */
class Recorder(
    private val uniqueId: UUID,
    private val players: List<UUID>
) : PacketAdapter(
    ScalaCommonsSpigot.instance,
    ListenerPriority.MONITOR,
    ReplayPacketBridge.packetTypesInternal
)
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
                ReplayPacketBridge
                    .converter()
                    .createLoginSuccessPacket(recordedPlayer)
            )

            savePacket(
                ReplayPacketBridge
                    .converter()
                    .createLoginPacket(recordedPlayer)
            )

            savePacket(
                ReplayPacketBridge
                    .converter()
                    .createPositionPacket(recordedPlayer.location)
            )

            savePacket(
                ReplayPacketBridge
                    .converter()
                    .createPlayerSpawnPacket(recordedPlayer)
            )

            savePacket(
                ReplayPacketBridge
                    .converter()
                    .createEntityEquipmentPacket(
                        recordedPlayer,
                        PacketEquipmentSlot.BOOTS,
                        recordedPlayer.equipment.boots
                    )
            )

            savePacket(
                ReplayPacketBridge
                    .converter()
                    .createEntityEquipmentPacket(
                        recordedPlayer,
                        PacketEquipmentSlot.LEGGINGS,
                        recordedPlayer.equipment.leggings
                    )
            )

            savePacket(
                ReplayPacketBridge
                    .converter()
                    .createEntityEquipmentPacket(
                        recordedPlayer,
                        PacketEquipmentSlot.CHESTPLATE,
                        recordedPlayer.equipment.chestplate
                    )
            )

            savePacket(
                ReplayPacketBridge
                    .converter()
                    .createEntityEquipmentPacket(
                        recordedPlayer,
                        PacketEquipmentSlot.HELMET,
                        recordedPlayer.equipment.helmet
                    )
            )

            savePacket(
                ReplayPacketBridge
                    .converter()
                    .createTeleportPacket(recordedPlayer, recordedPlayer.location)
            )
        }
    }

    fun initialize()
    {
        RecorderStorage.recorders[uniqueId] = this
        Protocol.manager().addPacketListener(this)

        Events.subscribe(PlayerRespawnEvent::class.java, EventPriority.MONITOR)
            .filter { active() && this.players.contains(it.player.uniqueId) }
            .handler {
                savePacket(
                    ReplayPacketBridge
                        .converter()
                        .createPlayerSpawnPacket(
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
                    ReplayPacketBridge
                        .converter()
                        .createEntityDamagePacket(
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
                    ReplayPacketBridge
                        .converter()
                        .createEntityDamagePacket(
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
                    ReplayPacketBridge
                        .converter()
                        .createTeleportPacket(
                            event.player, event.to
                        )
                )
            }
            .bindWith(terminable)

        Events.subscribe(PlayerItemHeldEvent::class.java, EventPriority.MONITOR)
            .filter { active() && this.players.contains(it.player.uniqueId) }
            .handler { event ->
                savePacket(
                    ReplayPacketBridge
                        .converter()
                        .createEntityEquipmentPacket(
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
                    ReplayPacketBridge
                        .converter()
                        .createEntityAnimationPacket(
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
                        ReplayPacketBridge
                            .converter()
                            .createHeadRotationPacket(
                                event.player, event.to.yaw
                            )
                    )
                }

                val move = event.to
                    .clone().subtract(event.from).toVector()

                if (ReplayPacketUtils.differentLocation(event.from, event.to))
                {
                    savePacket(
                        ReplayPacketBridge
                            .converter()
                            .createPositionLookPacket(
                                event.player, move,
                                event.to.yaw, event.to.pitch
                            )
                    )
                } else if (ReplayPacketUtils.hasRotated(event.from, event.to))
                {
                    savePacket(
                        ReplayPacketBridge
                            .converter()
                            .createLookPacket(
                                event.player, event.to.yaw, event.to.pitch
                            )
                    )
                } else
                {
                    savePacket(
                        ReplayPacketBridge
                            .converter()
                            .createPositionPacket(event.player, move)
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
                ReplayPacketBridge
                    .converter()
                    .createDestroyPacket(
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

        Protocol.manager()
            .removePacketListener(this)

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

    override fun onPacketReceiving(event: PacketEvent)
    {
        if (active() && this.players.contains(event.player.uniqueId))
        {
            if (event.packetType === PacketType.Play.Client.ARM_ANIMATION)
            {
                savePacket(
                    ReplayPacketBridge
                        .converter()
                        .convertAnimationPacket(event.player, event.packet)
                )
            }
        }
    }

    override fun onPacketSending(event: PacketEvent)
    {
        if (active() && this.players.contains(event.player.uniqueId))
        {
            savePacket(event.packet)
        }
    }

    private fun savePacket(
        container: PacketContainer
    )
    {
        val delta = (System.currentTimeMillis() - this.metadata.timestamps.beginning).toInt()

        val wrapper =
            ReplayPacketWrapper(
                delta, container
            )

        this.packets.add(wrapper)
    }
}
