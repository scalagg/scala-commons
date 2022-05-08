package gg.scala.commons.rassist.dao.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

/**
 * @author GrowlyX
 * @since 5/7/2022
 */
interface ReplayPacketConverter
{
    fun getPacketID(type: PacketType): Int
    fun getPacketType(id: Int): PacketType

    fun createLoginSuccessPacket(player: Player): PacketContainer
    fun createLoginPacket(player: Player): PacketContainer
    fun createPlayerListItemPacket(player: Player): PacketContainer
    fun createPlayerSpawnPacket(player: Player): PacketContainer

    fun createPlayerSpawnPacket(
        player: Player,
        location: Location
    ): PacketContainer

    fun createPositionPacket(position: Location): PacketContainer
    fun createPositionPacket(player: Player, move: Vector): PacketContainer

    fun createPositionLookPacket(
        player: Player,
        move: Vector,
        yaw: Float,
        pitch: Float
    ): PacketContainer

    fun createLookPacket(player: Player, yaw: Float, pitch: Float): PacketContainer
    fun createTeleportPacket(player: Player, location: Location): PacketContainer
    fun createHeadRotationPacket(player: Player, yaw: Float): PacketContainer
    fun convertAnimationPacket(player: Player, oldPacket: PacketContainer): PacketContainer
}
