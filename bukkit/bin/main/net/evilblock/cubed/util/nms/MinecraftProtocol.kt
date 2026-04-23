/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.nms

import gg.scala.commons.tablist.util.Util
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.ServerVersion
import net.evilblock.cubed.util.bukkit.AngleUtils
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.math.floor

object MinecraftProtocol
{
    private val ENTITY_DATA_ACCESSOR_CLASS = Reflection.getClassSuppressed("net.minecraft.network.syncher.EntityDataAccessor")
    private val DATA_VALUE_CLASS = Reflection.getClassSuppressed("net.minecraft.network.syncher.SynchedEntityData\$DataValue")
    private val DATA_VALUE_CREATE_METHOD = if (ENTITY_DATA_ACCESSOR_CLASS != null && DATA_VALUE_CLASS != null)
    {
        Reflection.getMethodSuppressed(DATA_VALUE_CLASS, "create", ENTITY_DATA_ACCESSOR_CLASS, Any::class.java)
    }
    else null

    private val COMPONENT_CLASS = Reflection.getClassSuppressed("net.minecraft.network.chat.Component")
    private val COMPONENT_LITERAL_METHOD = COMPONENT_CLASS?.let {
        Reflection.getMethodSuppressed(it, "literal", String::class.java)
    }

    private val PLAYER_CONNECTION_FIELD = Reflection
        .getDeclaredField(
            MinecraftReflection.ENTITY_PLAYER_CLASS,
            if (MinecraftReflection.isModern())
                "connection" else "playerConnection"
        )!!

    private val PACKET_CLASS = Reflection.getClassSuppressed("net.minecraft.network.protocol.Packet")
        ?: MinecraftReflection.getNMSClass("Packet")

    private val SEND_PACKET_METHOD = Reflection.getMethodSuppressed(
        PLAYER_CONNECTION_FIELD.type,
        "send",
        PACKET_CLASS!!
    ) ?: Reflection.getMethodSuppressed(
        PLAYER_CONNECTION_FIELD.type,
        "sendPacket",
        PACKET_CLASS!!
    ) ?: Reflection.getMethodSuppressed(
        PLAYER_CONNECTION_FIELD.type,
        "a",
        PACKET_CLASS!!
    )

    private val BLOCK_POSITION_CLASS =
        if (MinecraftReflection.isModern())
        {
            Reflection.getClassSuppressed(
                "net.minecraft.core.BlockPosition"
            )!!
        } else
        {
            MinecraftReflection.getNMSClass("BlockPosition")!!
        }

    private val BLOCK_POSITION_CONSTRUCTOR =
        Reflection.getConstructor(
            BLOCK_POSITION_CLASS,
            Int::class.java,
            Int::class.java,
            Int::class.java
        )

    @JvmStatic
    fun newPacket(name: String): Any
    {
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
    fun getPacketClass(
        name: String
    ): Class<*>
    {
        return if (MinecraftReflection.isModern())
        {
            Reflection.getClassSuppressed(
                "net.minecraft.network.protocol.game.$name"
            )!!
        } else
        {
            MinecraftReflection.getNMSClass(name)!!
        }
    }

    @JvmStatic
    fun send(player: Player, packet: Any)
    {
        if (!MinecraftReflection.isModern())
        {
            Util.sendPacket(player, packet)
            return
        }

        val handle = MinecraftReflection.getHandle(player)
        val connection = PLAYER_CONNECTION_FIELD.get(handle)
        SEND_PACKET_METHOD!!.invoke(connection, packet)
    }

    @JvmStatic
    fun send(player: Player, vararg packets: Any)
    {
        for (packet in packets)
        {
            send(player, packet)
        }
    }

    @JvmStatic
    fun newBlockPosition(x: Int, y: Int, z: Int): Any
    {
        return BLOCK_POSITION_CONSTRUCTOR?.newInstance(x, y, z)
            ?: throw IllegalStateException("BlockPosition not supported on this server version")
    }

    @JvmStatic
    fun newBlockActionPacket(block: Block, action: Int, param: Int): Any
    {
        val packet = newPacket("PacketPlayOutBlockAction")

        if (ServerVersion.getVersion().isNewerThan(ServerVersion.v1_12_R2))
        {
            Reflection.setDeclaredFieldValue(packet, "a", newBlockPosition(block.x, block.y, block.z))
            Reflection.setDeclaredFieldValue(packet, "b", action)
            Reflection.setDeclaredFieldValue(packet, "c", param)
            Reflection.setDeclaredFieldValue(packet, "d", MinecraftReflection.getHandle(block))
        } else
        {
            Reflection.setDeclaredFieldValue(packet, "a", block.x)
            Reflection.setDeclaredFieldValue(packet, "b", block.y)
            Reflection.setDeclaredFieldValue(packet, "c", block.z)
            Reflection.setDeclaredFieldValue(packet, "d", action)
            Reflection.setDeclaredFieldValue(packet, "e", param)
            Reflection.setDeclaredFieldValue(packet, "f", MinecraftReflection.getHandle(block))
        }

        return packet
    }

    fun buildEntityHeadRotationPacket(id: Int, yaw: Float): Any
    {
        if (MinecraftReflection.isModern())
        {
            return buildEntityLookPacket(id, yaw, 0.0f)
        }

        val packet = newPacket("PacketPlayOutEntityHeadRotation")

        Reflection.setDeclaredFieldValue(packet, "a", id)
        Reflection.setDeclaredFieldValue(packet, "b", AngleUtils.yawToBytes(yaw))

        return packet
    }

    fun buildEntityLookPacket(id: Int, yaw: Float, pitch: Float): Any
    {
        if (MinecraftReflection.isModern())
        {
            val packetClass = Reflection.getClassSuppressed("net.minecraft.network.protocol.game.ClientboundMoveEntityPacket\$Rot")!!
            val constructor = packetClass.declaredConstructors.first { it.parameterTypes.size == 4 }
            constructor.isAccessible = true
            return constructor.newInstance(id, AngleUtils.yawToBytes(yaw), AngleUtils.yawToBytes(pitch), true)
        }

        val packet = newPacket("PacketPlayOutEntity\$PacketPlayOutEntityLook")
        val owningClass = getPacketClass("PacketPlayOutEntity")

        Reflection.setDeclaredFieldValue(owningClass, packet, "a", id)
        Reflection.setDeclaredFieldValue(owningClass, packet, "e", AngleUtils.yawToBytes(yaw))
        Reflection.setDeclaredFieldValue(owningClass, packet, "f", AngleUtils.yawToBytes(pitch))
        Reflection.setDeclaredFieldValue(owningClass, packet, "g", true)

        return packet
    }

    fun buildEntityRelMovePacket(id: Int, from: Location, to: Location): Any
    {
        val packet = newPacket("PacketPlayOutEntity\$PacketPlayOutRelEntityMove")
        val owningClass = getPacketClass("PacketPlayOutEntity")

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

    fun buildEntityRelMoveLookPacket(id: Int, from: Location, to: Location): Any
    {
        val packet = newPacket("PacketPlayOutEntity\$PacketPlayOutRelEntityMoveLook")
        val owningClass = getPacketClass("PacketPlayOutEntity")

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

    fun buildEntityTeleportPacket(id: Int, location: Location): Any
    {
        if (MinecraftReflection.isModern())
        {
            val vec3Class = Reflection.getClassSuppressed("net.minecraft.world.phys.Vec3")!!
            val vec3Constructor = vec3Class.declaredConstructors.first { it.parameterTypes.size == 3 }
            vec3Constructor.isAccessible = true
            val vec3 = vec3Constructor.newInstance(location.x, location.y, location.z)
            val zero = Reflection.getDeclaredField(vec3Class, "ZERO")!!.get(null)
            val pmrClass = Reflection.getClassSuppressed("net.minecraft.world.entity.PositionMoveRotation")!!
            val pmrConstructor = pmrClass.declaredConstructors.first { it.parameterTypes.size == 4 }
            pmrConstructor.isAccessible = true
            val positionMoveRotation = pmrConstructor.newInstance(vec3, zero, location.yaw, location.pitch)
            val packetClass = Reflection.getClassSuppressed("net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket")!!
            val teleport = packetClass.declaredMethods.first { method ->
                method.name == "teleport" && method.parameterTypes.size == 4
            }
            teleport.isAccessible = true
            return teleport.invoke(null, id, positionMoveRotation, emptySet<Any>(), true)!!
        }

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
    fun buildEntityEquipmentPacket(id: Int, slot: Int, itemStack: ItemStack): Any
    {
        if (MinecraftReflection.isModern())
        {
            return buildModernEntityEquipmentPacket(id, slot, itemStack)
        }

        val packet = newPacket("PacketPlayOutEntityEquipment")
        Reflection.setDeclaredFieldValue(packet, "a", id)
        Reflection.setDeclaredFieldValue(packet, "b", slot)
        Reflection.setDeclaredFieldValue(packet, "c", ItemUtils.getNmsCopy(itemStack))
        return packet
    }

    @JvmStatic
    fun buildEntityEquipmentPacket(id: Int, slot: Any, itemStack: ItemStack): Any
    {
        if (MinecraftReflection.isModern() && slot is Int)
        {
            return buildModernEntityEquipmentPacket(id, slot, itemStack)
        }

        val packet = newPacket("PacketPlayOutEntityEquipment")
        Reflection.setDeclaredFieldValue(packet, "a", id)
        Reflection.setDeclaredFieldValue(packet, "b", slot)
        Reflection.setDeclaredFieldValue(packet, "c", ItemUtils.getNmsCopy(itemStack))
        return packet
    }

    @JvmStatic
    fun buildSpawnEntityLivingPacket(id: Int, uuid: UUID, typeId: Int, location: Location): Any
    {
        if (MinecraftReflection.isModern())
        {
            val typeName = when (typeId)
            {
                30 -> "ARMOR_STAND"
                120 -> "VILLAGER"
                else -> throw IllegalArgumentException("Unsupported modern entity type id: $typeId")
            }

            return buildModernAddEntityPacket(id, uuid, typeName, location)
        }

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

    private fun convert(double: Double): Long
    {
        val var2 = double.toLong()
        return if (double < var2.toDouble()) var2 - 1L else var2
    }

    @JvmStatic
    fun getPlayerVersion(player: Player): Int
    {
        return ScalaCommonsSpigot.instance.versionProvider.getProtocolVersion(player)
    }

    @JvmStatic
    fun buildModernAddEntityPacket(id: Int, uuid: UUID, typeName: String, location: Location): Any
    {
        val entityTypeClass = Reflection.getClassSuppressed("net.minecraft.world.entity.EntityType")!!
        val entityType = Reflection.getDeclaredField(entityTypeClass, typeName)?.get(null)
            ?: Reflection.getField(entityTypeClass, typeName)?.get(null)
            ?: throw IllegalStateException("Missing EntityType.$typeName")
        val vec3Class = Reflection.getClassSuppressed("net.minecraft.world.phys.Vec3")!!
        val zero = Reflection.getDeclaredField(vec3Class, "ZERO")!!.get(null)
        val packetClass = Reflection.getClassSuppressed("net.minecraft.network.protocol.game.ClientboundAddEntityPacket")!!
        val constructor = packetClass.constructors.firstOrNull { it.parameterTypes.size == 11 }
            ?: throw IllegalStateException("Missing ClientboundAddEntityPacket ctor")
        return constructor.newInstance(
            id,
            uuid,
            location.x,
            location.y,
            location.z,
            location.pitch,
            location.yaw,
            entityType,
            0,
            zero,
            location.yaw.toDouble()
        )
    }

    @JvmStatic
    fun createEntityDataValue(ownerClassName: String, fieldName: String, value: Any): Any
    {
        val ownerClass = Reflection.getClassSuppressed(ownerClassName)!!
        val accessor = Reflection.getDeclaredField(ownerClass, fieldName)!!.get(null)
        if (DATA_VALUE_CREATE_METHOD != null)
        {
            return DATA_VALUE_CREATE_METHOD.invoke(null, accessor, value)!!
        }

        val serializer = Reflection.callMethod(accessor, "serializer")!!
        val id = Reflection.callMethod(accessor, "id") as Int
        val dataValueConstructor = DATA_VALUE_CLASS!!.declaredConstructors.first { it.parameterTypes.size == 3 }
        dataValueConstructor.isAccessible = true
        return dataValueConstructor.newInstance(id, serializer, value)
    }

    @JvmStatic
    fun buildModernEntityMetadataPacket(id: Int, values: List<Any>): Any
    {
        val packetClass = Reflection.getClassSuppressed("net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket")!!
        val constructor = packetClass.declaredConstructors.first {
            it.parameterTypes.size == 2 && it.parameterTypes[1] == List::class.java
        }
        constructor.isAccessible = true
        return constructor.newInstance(id, values)
    }

    @JvmStatic
    fun buildModernRemoveEntitiesPacket(vararg ids: Int): Any
    {
        val packetClass = Reflection.getClassSuppressed("net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket")!!
        return Reflection.callConstructor(packetClass, ids)!!
    }

    @JvmStatic
    fun createLiteralComponent(text: String): Any
    {
        return COMPONENT_LITERAL_METHOD!!.invoke(null, text)!!
    }

    private fun buildModernEntityEquipmentPacket(id: Int, slot: Int, itemStack: ItemStack): Any
    {
        val packetClass = Reflection.getClassSuppressed("net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket")!!
        val pairClass = Reflection.getClassSuppressed("com.mojang.datafixers.util.Pair")!!
        val equipmentSlotClass = Reflection.getClassSuppressed("net.minecraft.world.entity.EquipmentSlot")!!
        val modernSlotName = when (slot)
        {
            0 -> "MAINHAND"
            1 -> "FEET"
            2 -> "LEGS"
            3 -> "CHEST"
            4 -> "HEAD"
            else -> throw IllegalArgumentException("Unsupported equipment slot index: $slot")
        }

        @Suppress("UNCHECKED_CAST")
        val slotEnum = java.lang.Enum.valueOf(equipmentSlotClass as Class<out Enum<*>>, modernSlotName)
        val nmsItem = ItemUtils.getNmsCopy(itemStack)
        val pair = Reflection.getMethodSuppressed(pairClass, "of", Any::class.java, Any::class.java)
            ?.invoke(null, slotEnum, nmsItem)
            ?: run {
                val pairConstructor = pairClass.declaredConstructors.first { it.parameterTypes.size == 2 }
                pairConstructor.isAccessible = true
                pairConstructor.newInstance(slotEnum, nmsItem)
            }
        val constructor = packetClass.declaredConstructors.first {
            it.parameterTypes.size >= 2 && it.parameterTypes[0] == Int::class.javaPrimitiveType && it.parameterTypes[1] == List::class.java
        }
        constructor.isAccessible = true
        return if (constructor.parameterCount == 2) constructor.newInstance(id, listOf(pair))
        else constructor.newInstance(id, listOf(pair), false)
    }

}
