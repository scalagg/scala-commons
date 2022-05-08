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
import gg.scala.commons.rassist.packet.ReplayPacketBridge
import gg.scala.commons.rassist.util.ReplayPacketUtils
import me.lucko.helper.Events
import me.lucko.helper.protocol.Protocol
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.ScalaCommonsSpigot
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.util.Vector
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
        mutableMapOf<UUID, LinkedList<ReplayPacketWrapper>>()
            .apply {
                for (player in players)
                {
                    this[player] = LinkedList()
                }
            }

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
                recordedPlayer,
                ReplayPacketBridge
                    .converter()
                    .createLoginSuccessPacket(recordedPlayer)
            )

            savePacket(
                recordedPlayer,
                ReplayPacketBridge
                    .converter()
                    .createLoginPacket(recordedPlayer)
            )

            savePacket(
                recordedPlayer,
                ReplayPacketBridge
                    .converter()
                    .createPositionPacket(recordedPlayer.location)
            )

            savePacket(
                recordedPlayer,
                ReplayPacketBridge
                    .converter()
                    .createPlayerListItemPacket(recordedPlayer)
            )

            savePacket(
                recordedPlayer,
                ReplayPacketBridge
                    .converter()
                    .createPlayerSpawnPacket(recordedPlayer)
            )

            savePacket(
                recordedPlayer,
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
            .filter { active() && this.packets.containsKey(it.player.uniqueId) }
            .handler {
                savePacket(
                    it.player,
                    ReplayPacketBridge
                        .converter()
                        .createPlayerSpawnPacket(
                            it.player, it.respawnLocation
                        )
                )
            }
            .bindWith(terminable)

        Events.subscribe(PlayerTeleportEvent::class.java, EventPriority.MONITOR)
            .filter { active() && this.packets.containsKey(it.player.uniqueId) }
            .handler { event ->
                savePacket(
                    event.player,
                    ReplayPacketBridge
                        .converter()
                        .createTeleportPacket(
                            event.player, event.to
                        )
                )
            }
            .bindWith(terminable)

        Events.subscribe(PlayerMoveEvent::class.java, EventPriority.MONITOR)
            .filter { active() && this.packets.containsKey(it.player.uniqueId) }
            .handler { event ->
                if (event.from.yaw != event.to.yaw)
                {
                    savePacket(
                        event.player,
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
                        event.player,
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
                        event.player,
                        ReplayPacketBridge
                            .converter()
                            .createLookPacket(
                                event.player, event.to.yaw, event.to.pitch
                            )
                    )
                } else
                {
                    savePacket(
                        event.player,
                        ReplayPacketBridge
                            .converter()
                            .createPositionPacket(event.player, move)
                    )
                }
            }
            .bindWith(terminable)
    }

    fun stopAndSave(): CompletableFuture<Void>
    {
        this.metadata.timestamps.ending =
            System.currentTimeMillis()

        val replay = Replay(
            this.metadata,
            this.packets
        )

        this.terminable
            .closeAndReportException()

        Protocol.manager()
            .removePacketListener(this)

        return CompletableFuture
            .runAsync {
                RAssistBridge.storage()
                    .persist(this.uniqueId, replay)
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
        if (active() && this.packets.containsKey(event.player.uniqueId))
        {
            if (event.packetType === PacketType.Play.Client.ARM_ANIMATION)
            {
                savePacket(
                    event.player,
                    ReplayPacketBridge
                        .converter()
                        .convertAnimationPacket(event.player, event.packet)
                )
            }
        }
    }

    override fun onPacketSending(event: PacketEvent)
    {
        if (active() && this.packets.containsKey(event.player.uniqueId))
        {
            savePacket(event.player, event.packet)
        }
    }

    private fun savePacket(
        player: Player,
        container: PacketContainer
    )
    {
        val delta = (System.currentTimeMillis() - this.metadata.timestamps.beginning).toInt()

        val wrapper =
            ReplayPacketWrapper(
                delta, container
            )

        this.packets[player.uniqueId]
            ?.add(wrapper)
    }
}
