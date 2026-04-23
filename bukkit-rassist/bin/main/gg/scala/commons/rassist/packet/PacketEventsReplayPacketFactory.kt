package gg.scala.commons.rassist.packet

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.entity.EntityPositionData
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon
import com.github.retrooper.packetevents.protocol.player.Equipment
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot
import com.github.retrooper.packetevents.protocol.player.GameMode
import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.protocol.player.UserProfile
import com.github.retrooper.packetevents.protocol.teleport.RelativeFlag
import com.github.retrooper.packetevents.protocol.world.Difficulty
import com.github.retrooper.packetevents.protocol.world.WorldBlockPosition
import com.github.retrooper.packetevents.protocol.world.dimension.DimensionTypes
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import com.github.retrooper.packetevents.util.Vector3d
import com.github.retrooper.packetevents.util.Vector3i
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.login.server.WrapperLoginServerLoginSuccess
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityHeadLook
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMove
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMoveAndRotation
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRotation
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityStatus
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer
import gg.scala.commons.rassist.dao.packet.ReplayPacketWrapper
import gg.scala.commons.rassist.dao.packet.enums.PacketAnimationType
import gg.scala.commons.rassist.dao.packet.enums.PacketEquipmentSlot
import gg.scala.commons.tablist.util.Util
import io.netty.buffer.Unpooled
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.EntityEffect
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

object PacketEventsReplayPacketFactory
{
    private fun currentClientVersion() = PacketEvents.getAPI().serverManager.version.toClientVersion()

    private fun serialize(time: Int, packetType: PacketTypeCommon, wrapper: PacketWrapper<*>): ReplayPacketWrapper
    {
        val buffer = Unpooled.buffer()
        wrapper.serverVersion = PacketEvents.getAPI().serverManager.version
        wrapper.clientVersion = currentClientVersion()
        wrapper.buffer = buffer
        wrapper.write()

        val bytes = ByteArray(buffer.readableBytes())
        buffer.readBytes(bytes)
        buffer.release()
        return ReplayPacketWrapper(time, packetType.getId(currentClientVersion()), bytes)
    }

    private fun createProfile(player: Player): UserProfile
    {
        val profile = Util.getPlayerProfile(player)
        val userProfile = UserProfile(profile.id, profile.name)
        profile.skinData?.let {
            userProfile.textureProperties.add(TextureProperty("textures", it.value, it.signature))
        }
        return userProfile
    }

    fun createLoginSuccessPacket(time: Int, player: Player): ReplayPacketWrapper
    {
        val wrapper = WrapperLoginServerLoginSuccess(createProfile(player))
        return serialize(time, PacketType.Login.Server.LOGIN_SUCCESS, wrapper)
    }

    fun createLoginPacket(time: Int, player: Player): ReplayPacketWrapper
    {
        val wrapper = WrapperPlayServerJoinGame(
            player.entityId + 3000,
            false,
            GameMode.SURVIVAL,
            null,
            listOf(player.world.name),
            NBTCompound(),
            DimensionTypes.OVERWORLD,
            Difficulty.getById(player.world.difficulty.value),
            player.world.name,
            0L,
            Bukkit.getMaxPlayers(),
            10,
            10,
            false,
            true,
            false,
            false,
            false,
            null,
            null
        )
        return serialize(time, PacketType.Play.Server.JOIN_GAME, wrapper)
    }

    fun createPositionPacket(time: Int, position: Location): ReplayPacketWrapper
    {
        val wrapper = WrapperPlayServerPlayerPositionAndLook(
            Vector3d(position.x, position.y, position.z),
            position.yaw,
            position.pitch,
            0,
            0
        )
        return serialize(time, PacketType.Play.Server.PLAYER_POSITION_AND_LOOK, wrapper)
    }

    fun createPlayerSpawnPacket(time: Int, player: Player): ReplayPacketWrapper =
        createPlayerSpawnPacket(time, player, player.location)

    fun createPlayerSpawnPacket(time: Int, player: Player, location: Location): ReplayPacketWrapper
    {
        val wrapper = WrapperPlayServerSpawnPlayer(
            player.entityId + 3000,
            player.uniqueId,
            SpigotConversionUtil.fromBukkitLocation(location),
            emptyList()
        )
        return serialize(time, PacketType.Play.Server.SPAWN_PLAYER, wrapper)
    }

    fun createDestroyPacket(time: Int, player: Player): ReplayPacketWrapper
    {
        val wrapper = WrapperPlayServerDestroyEntities(player.entityId + 3000)
        return serialize(time, PacketType.Play.Server.DESTROY_ENTITIES, wrapper)
    }

    fun createEntityDamagePacket(time: Int, player: Player, effect: EntityEffect): ReplayPacketWrapper
    {
        val wrapper = WrapperPlayServerEntityStatus(player.entityId + 3000, effect.data.toInt())
        return serialize(time, PacketType.Play.Server.ENTITY_STATUS, wrapper)
    }

    fun createEntityEquipmentPacket(time: Int, player: Player, slot: PacketEquipmentSlot, item: ItemStack?): ReplayPacketWrapper
    {
        val equipmentSlot = when (slot) {
            PacketEquipmentSlot.HAND -> EquipmentSlot.MAIN_HAND
            PacketEquipmentSlot.BOOTS -> EquipmentSlot.BOOTS
            PacketEquipmentSlot.LEGGINGS -> EquipmentSlot.LEGGINGS
            PacketEquipmentSlot.CHESTPLATE -> EquipmentSlot.CHEST_PLATE
            PacketEquipmentSlot.HELMET -> EquipmentSlot.HELMET
        }
        val wrapper = WrapperPlayServerEntityEquipment(
            player.entityId + 3000,
            listOf(Equipment(equipmentSlot, SpigotConversionUtil.fromBukkitItemStack(item ?: org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR))))
        )
        return serialize(time, PacketType.Play.Server.ENTITY_EQUIPMENT, wrapper)
    }

    fun createEntityAnimationPacket(time: Int, player: Player, animation: PacketAnimationType): ReplayPacketWrapper
    {
        val type = when (animation) {
            PacketAnimationType.SWING_ARM -> WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM
            PacketAnimationType.EAT_FOOD -> WrapperPlayServerEntityAnimation.EntityAnimationType.HURT
            PacketAnimationType.CRITICAL -> WrapperPlayServerEntityAnimation.EntityAnimationType.CRITICAL_HIT
            PacketAnimationType.MAGIC_CRITICAL -> WrapperPlayServerEntityAnimation.EntityAnimationType.MAGIC_CRITICAL_HIT
        }
        val wrapper = WrapperPlayServerEntityAnimation(player.entityId + 3000, type)
        return serialize(time, PacketType.Play.Server.ENTITY_ANIMATION, wrapper)
    }

    fun createPositionPacket(time: Int, player: Player, move: Vector): ReplayPacketWrapper
    {
        val wrapper = WrapperPlayServerEntityRelativeMove(player.entityId + 3000, move.x, move.y, move.z, player.isOnGround)
        return serialize(time, PacketType.Play.Server.ENTITY_RELATIVE_MOVE, wrapper)
    }

    fun createPositionLookPacket(time: Int, player: Player, move: Vector, yaw: Float, pitch: Float): ReplayPacketWrapper
    {
        val wrapper = WrapperPlayServerEntityRelativeMoveAndRotation(player.entityId + 3000, move.x, move.y, move.z, yaw, pitch, player.isOnGround)
        return serialize(time, PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION, wrapper)
    }

    fun createLookPacket(time: Int, player: Player, yaw: Float, pitch: Float): ReplayPacketWrapper
    {
        val wrapper = WrapperPlayServerEntityRotation(player.entityId + 3000, yaw, pitch, player.isOnGround)
        return serialize(time, PacketType.Play.Server.ENTITY_ROTATION, wrapper)
    }

    fun createTeleportPacket(time: Int, player: Player, location: Location): ReplayPacketWrapper
    {
        val wrapper = WrapperPlayServerEntityTeleport(player.entityId + 3000, SpigotConversionUtil.fromBukkitLocation(location), player.isOnGround)
        return serialize(time, PacketType.Play.Server.ENTITY_TELEPORT, wrapper)
    }

    fun createHeadRotationPacket(time: Int, player: Player, yaw: Float): ReplayPacketWrapper
    {
        val wrapper = WrapperPlayServerEntityHeadLook(player.entityId + 3000, yaw)
        return serialize(time, PacketType.Play.Server.ENTITY_HEAD_LOOK, wrapper)
    }

    fun convertAnimationPacket(time: Int, player: Player): ReplayPacketWrapper
    {
        val wrapper = WrapperPlayServerEntityAnimation(player.entityId + 3000, WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM)
        return serialize(time, PacketType.Play.Server.ENTITY_ANIMATION, wrapper)
    }
}
