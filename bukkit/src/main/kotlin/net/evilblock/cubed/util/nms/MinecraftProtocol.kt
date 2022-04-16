/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.nms

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.utility.MinecraftVersion
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.AngleUtils
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.minecraft.server.level.EntityPlayer
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*
import kotlin.math.floor

object MinecraftProtocol {

    private val PLAYER_CONNECTION_FIELD = Reflection
        .getDeclaredField(
            MinecraftReflection.ENTITY_PLAYER_CLASS,
            if (MinecraftReflection.isModern())
            {
                "b"
            } else
            {
                "playerConnection"
            }
        )!!

    private val SEND_PACKET_METHOD =
        Reflection.getDeclaredMethod(
            MinecraftReflection.getNMSClass("PlayerConnection")!!,
            "sendPacket", MinecraftReflection.getNMSClass("Packet")!!
        )!!

    private val BLOCK_POSITION_CLASS = MinecraftReflection.getNMSClass("BlockPosition")
    private val BLOCK_POSITION_CONSTRUCTOR = BLOCK_POSITION_CLASS?.let { Reflection.getConstructor(it, Int::class.java, Int::class.java, Int::class.java) }

    @JvmStatic
    fun newPacket(name: String): Any {
        return Reflection.callConstructor(
            if (MinecraftReflection.isModern())
            {
                Reflection.getClassSuppressed(
                    "net.minecraft.network.protocol.game.$name"
                )!!
            } else
            {
                MinecraftReflection.getNMSClass(name)!!
            }
        )!!
    }

    @JvmStatic
    fun send(player: Player, packet: Any) {
        SEND_PACKET_METHOD.invoke(PLAYER_CONNECTION_FIELD.get(MinecraftReflection.getHandle(player)), packet)
    }

    @JvmStatic
    fun send(player: Player, vararg packets: Any) {
        for (packet in packets) {
            SEND_PACKET_METHOD.invoke(PLAYER_CONNECTION_FIELD.get(MinecraftReflection.getHandle(player)), packet)
        }
    }

    @JvmStatic
    fun newBlockPosition(x: Int, y: Int, z: Int): Any {
        return BLOCK_POSITION_CONSTRUCTOR?.newInstance(x, y, z) ?: throw IllegalStateException("BlockPosition not supported on this server version")
    }

    @JvmStatic
    fun newBlockActionPacket(block: Block, action: Int, param: Int): Any {
        // block action packet structure for 1.12
        // a = BlockPosition - location
        // b = int - action
        // c = int - parameter
        // d = Block - block

        // block action packet structure for 1.7.10
        // a = int - block x
        // b = int - block y
        // c = int - block z
        // d = int - action
        // e = int - param
        // f = Block - block
        val packet = newPacket("PacketPlayOutBlockAction")

        if (ProtocolLibrary.getProtocolManager().minecraftVersion.isAtLeast(MinecraftVersion.COLOR_UPDATE)) {
            Reflection.setDeclaredFieldValue(packet, "a", newBlockPosition(block.x, block.y, block.z))
            Reflection.setDeclaredFieldValue(packet, "b", action)
            Reflection.setDeclaredFieldValue(packet, "c", param)
            Reflection.setDeclaredFieldValue(packet, "d", MinecraftReflection.getHandle(block))
        } else {
            Reflection.setDeclaredFieldValue(packet, "a", block.x)
            Reflection.setDeclaredFieldValue(packet, "b", block.y)
            Reflection.setDeclaredFieldValue(packet, "c", block.z)
            Reflection.setDeclaredFieldValue(packet, "d", action)
            Reflection.setDeclaredFieldValue(packet, "e", param)
            Reflection.setDeclaredFieldValue(packet, "f", MinecraftReflection.getHandle(block))
        }

        return packet
    }

    fun buildEntityHeadRotationPacket(id: Int, yaw: Float): Any {
        val packet = newPacket("PacketPlayOutEntityHeadRotation")

        Reflection.setDeclaredFieldValue(packet, "a", id)
        Reflection.setDeclaredFieldValue(packet, "b", AngleUtils.yawToBytes(yaw))

        return packet
    }

    fun buildEntityLookPacket(id: Int, yaw: Float, pitch: Float): Any {
        val packet = newPacket("PacketPlayOutEntity\$PacketPlayOutEntityLook")
        val owningClass = MinecraftReflection.getNMSClass("PacketPlayOutEntity")!!

        Reflection.setDeclaredFieldValue(owningClass, packet, "a", id)
        Reflection.setDeclaredFieldValue(owningClass, packet, "e", AngleUtils.yawToBytes(yaw))
        Reflection.setDeclaredFieldValue(owningClass, packet, "f", AngleUtils.yawToBytes(pitch))
        Reflection.setDeclaredFieldValue(owningClass, packet, "g", true)

        return packet
    }

    fun buildEntityRelMovePacket(id: Int, from: Location, to: Location): Any {
        val packet = newPacket("PacketPlayOutEntity\$PacketPlayOutRelEntityMove")
        val owningClass = MinecraftReflection.getNMSClass("PacketPlayOutEntity")!!

        val fromX = convert(from.x * 4096.0)
        val fromY = convert(from.y * 4096.0)
        val fromZ = convert(from.z * 4096.0)

        val toX = convert(to.x * 4096.0)
        val toY = convert(to.y * 4096.0)
        val toZ = convert(to.z * 4096.0)

        val relativeX = (toX - fromX).toInt()
        val relativeY = (toY - fromY).toInt()
        val relativeZ = (toZ - fromZ).toInt()

        Reflection.setDeclaredFieldValue(owningClass, packet, "a", id)
        Reflection.setDeclaredFieldValue(owningClass, packet, "b", relativeX)
        Reflection.setDeclaredFieldValue(owningClass, packet, "c", relativeY)
        Reflection.setDeclaredFieldValue(owningClass, packet, "d", relativeZ)
        Reflection.setDeclaredFieldValue(owningClass, packet, "g", true)

        return packet
    }

    fun buildEntityRelMoveLookPacket(id: Int, from: Location, to: Location): Any {
        val packet = newPacket("PacketPlayOutEntity\$PacketPlayOutRelEntityMoveLook")
        val owningClass = MinecraftReflection.getNMSClass("PacketPlayOutEntity")!!

        val fromX = convert(from.x * 4096.0)
        val fromY = convert(from.y * 4096.0)
        val fromZ = convert(from.z * 4096.0)

        val toX = convert(to.x * 4096.0)
        val toY = convert(to.y * 4096.0)
        val toZ = convert(to.z * 4096.0)

        val relativeX = (toX - fromX).toInt()
        val relativeY = (toY - fromY).toInt()
        val relativeZ = (toZ - fromZ).toInt()

        Reflection.setDeclaredFieldValue(owningClass, packet, "a", id)
        Reflection.setDeclaredFieldValue(owningClass, packet, "b", relativeX)
        Reflection.setDeclaredFieldValue(owningClass, packet, "c", relativeY)
        Reflection.setDeclaredFieldValue(owningClass, packet, "d", relativeZ)
        Reflection.setDeclaredFieldValue(owningClass, packet, "e", AngleUtils.yawToBytes(to.yaw - from.yaw))
        Reflection.setDeclaredFieldValue(owningClass, packet, "f", AngleUtils.yawToBytes(to.pitch - from.pitch))
        Reflection.setDeclaredFieldValue(owningClass, packet, "g", true)

        return packet
    }

    fun buildEntityTeleportPacket(id: Int, location: Location): Any {
        val packet = newPacket("PacketPlayOutEntityTeleport")

        Reflection.setDeclaredFieldValue(packet, "a", id)
        Reflection.setDeclaredFieldValue(packet, "b", MinecraftReflection.mathFloor(location.x * 32.0))
        Reflection.setDeclaredFieldValue(packet, "c", MinecraftReflection.mathFloor(location.y * 32.0))
        Reflection.setDeclaredFieldValue(packet, "d", MinecraftReflection.mathFloor(location.z * 32.0))
        Reflection.setDeclaredFieldValue(packet, "e", AngleUtils.yawToBytes(location.yaw))
        Reflection.setDeclaredFieldValue(packet, "f", AngleUtils.yawToBytes(location.pitch))

        return packet
    }

    @JvmStatic
    fun buildEntityEquipmentPacket(id: Int, slot: Int, itemStack: ItemStack): Any {
        val packet = newPacket("PacketPlayOutEntityEquipment")
        Reflection.setDeclaredFieldValue(packet, "a", id)
        Reflection.setDeclaredFieldValue(packet, "b", slot)
        Reflection.setDeclaredFieldValue(packet, "c", ItemUtils.getNmsCopy(itemStack))
        return packet
    }

    @JvmStatic
    fun buildEntityEquipmentPacket(id: Int, slot: Any, itemStack: ItemStack): Any {
        val packet = newPacket("PacketPlayOutEntityEquipment")
        Reflection.setDeclaredFieldValue(packet, "a", id)
        Reflection.setDeclaredFieldValue(packet, "b", slot)
        Reflection.setDeclaredFieldValue(packet, "c", ItemUtils.getNmsCopy(itemStack))
        return packet
    }

    @JvmStatic
    fun buildSpawnEntityLivingPacket(id: Int, uuid: UUID, typeId: Int, location: Location): Any {
        return newPacket("PacketPlayOutSpawnEntityLiving").also { packet ->
            Reflection.setDeclaredFieldValue(packet, "a", id)
            Reflection.setDeclaredFieldValue(packet, "b", 30)
            Reflection.setDeclaredFieldValue(packet, "c", floor(location.x * 32.0))
            Reflection.setDeclaredFieldValue(packet, "d", floor(location.y * 32.0))
            Reflection.setDeclaredFieldValue(packet, "e", floor(location.z * 32.0))
            Reflection.setDeclaredFieldValue(packet, "i", AngleUtils.yawToBytes(location.yaw))
            Reflection.setDeclaredFieldValue(packet, "j", AngleUtils.yawToBytes(location.pitch))
        }
    }

    private fun convert(double: Double): Long {
        val var2 = double.toLong()
        return if (double < var2.toDouble()) var2 - 1L else var2
    }

    @JvmStatic
    fun getPlayerVersion(player: Player): Int {
        return ProtocolLibrary.getProtocolManager().getProtocolVersion(player)
    }

}
