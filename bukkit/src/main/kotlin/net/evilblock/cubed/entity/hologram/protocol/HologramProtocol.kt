/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.hologram.protocol

import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.nms.DataWatcherUtil
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.cubed.util.nms.MinecraftReflection
import org.bukkit.entity.Player

object HologramProtocol {

    fun sendSpawnPackets(player: Player, hologram: HologramEntity) {
        val hologramText = hologram.processPlaceholders(player, hologram.getText())

        val dataWatcher = DataWatcherUtil.new()
        DataWatcherUtil.register(dataWatcher, 0, 0.toByte()) // flag
        DataWatcherUtil.register(dataWatcher, 1, 0.toShort()) // air ticks
        DataWatcherUtil.register(dataWatcher, 2, hologramText) // custom name
        DataWatcherUtil.register(dataWatcher, 3, 1.toByte()) // custom name visible
        DataWatcherUtil.register(dataWatcher, 4, 0.toByte()) // silent

        DataWatcherUtil.setFlag(dataWatcher, 5, true)

        DataWatcherUtil.register(dataWatcher, 6, 1.toFloat()) // health
        DataWatcherUtil.register(dataWatcher, 7, 0)
        DataWatcherUtil.register(dataWatcher, 8, 0.toByte())
        DataWatcherUtil.register(dataWatcher, 9, 0.toByte()) // arrow count

        DataWatcherUtil.register(dataWatcher, 10, 0.toByte())

        DataWatcherUtil.setTypeFlag(dataWatcher, 10, 1, true)
        DataWatcherUtil.setTypeFlag(dataWatcher, 10, 4, false)
        DataWatcherUtil.setTypeFlag(dataWatcher, 10, 8, false)
        DataWatcherUtil.setTypeFlag(dataWatcher, 10, 10, true)

        DataWatcherUtil.register(dataWatcher, 11, MinecraftReflection.ARMOR_STAND_HEAD_POSE.clone().vector)
        DataWatcherUtil.register(dataWatcher, 12, MinecraftReflection.ARMOR_STAND_BODY_POSE.clone().vector)
        DataWatcherUtil.register(dataWatcher, 13, MinecraftReflection.ARMOR_STAND_LEFT_ARM_POSE.clone().vector)
        DataWatcherUtil.register(dataWatcher, 14, MinecraftReflection.ARMOR_STAND_RIGHT_ARM_POSE.clone().vector)
        DataWatcherUtil.register(dataWatcher, 15, MinecraftReflection.ARMOR_STAND_LEFT_LEG_POSE.clone().vector)
        DataWatcherUtil.register(dataWatcher, 16, MinecraftReflection.ARMOR_STAND_RIGHT_LEG_POSE.clone().vector)

        val spawnPacket = MinecraftProtocol.newPacket("PacketPlayOutSpawnEntityLiving")
        Reflection.setDeclaredFieldValue(spawnPacket, "a", hologram.id)
        Reflection.setDeclaredFieldValue(spawnPacket, "b", 30)
        Reflection.setDeclaredFieldValue(spawnPacket, "l", dataWatcher)

        val location = hologram.getAdjustedLocation(player)
        Reflection.setDeclaredFieldValue(spawnPacket, "c", MinecraftReflection.mathFloor(location.x * 32.0))
        Reflection.setDeclaredFieldValue(spawnPacket, "d", MinecraftReflection.mathFloor(location.y * 32.0))
        Reflection.setDeclaredFieldValue(spawnPacket, "e", MinecraftReflection.mathFloor(location.z * 32.0))

        MinecraftProtocol.send(player, spawnPacket)
    }

    fun sendUpdatePackets(player: Player, hologram: HologramEntity) {

        val hologramText = hologram.processPlaceholders(player, hologram.getText())

        val dataWatcher = DataWatcherUtil.new()
        DataWatcherUtil.register(dataWatcher, 0, 0.toByte()) // flag
        DataWatcherUtil.register(dataWatcher, 1, 0.toShort()) // air ticks
        DataWatcherUtil.register(dataWatcher, 2, hologramText) // custom name
        DataWatcherUtil.register(dataWatcher, 3, 1.toByte()) // custom name visible
        DataWatcherUtil.register(dataWatcher, 4, 0.toByte()) // silent

        DataWatcherUtil.setFlag(dataWatcher, 5, true)

        DataWatcherUtil.register(dataWatcher, 6, 1.toFloat()) // health
        DataWatcherUtil.register(dataWatcher, 7, 0)
        DataWatcherUtil.register(dataWatcher, 8, 0.toByte())
        DataWatcherUtil.register(dataWatcher, 9, 0.toByte()) // arrow count

        DataWatcherUtil.register(dataWatcher, 10, 0.toByte())
        DataWatcherUtil.setTypeFlag(dataWatcher, 10, 1, true)
        DataWatcherUtil.setTypeFlag(dataWatcher, 10, 4, false)
        DataWatcherUtil.setTypeFlag(dataWatcher, 10, 8, false)
        DataWatcherUtil.setTypeFlag(dataWatcher, 10, 10, true)

        DataWatcherUtil.register(dataWatcher, 11, MinecraftReflection.ARMOR_STAND_HEAD_POSE.clone().vector)
        DataWatcherUtil.register(dataWatcher, 12, MinecraftReflection.ARMOR_STAND_BODY_POSE.clone().vector)
        DataWatcherUtil.register(dataWatcher, 13, MinecraftReflection.ARMOR_STAND_LEFT_ARM_POSE.clone().vector)
        DataWatcherUtil.register(dataWatcher, 14, MinecraftReflection.ARMOR_STAND_RIGHT_ARM_POSE.clone().vector)
        DataWatcherUtil.register(dataWatcher, 15, MinecraftReflection.ARMOR_STAND_LEFT_LEG_POSE.clone().vector)
        DataWatcherUtil.register(dataWatcher, 16, MinecraftReflection.ARMOR_STAND_RIGHT_LEG_POSE.clone().vector)

        val metadataPacket = MinecraftProtocol.newPacket("PacketPlayOutEntityMetadata")
        Reflection.setDeclaredFieldValue(metadataPacket, "a", hologram.id)
        Reflection.setDeclaredFieldValue(metadataPacket, "b", DataWatcherUtil.getWatchedObjects(dataWatcher) as Any)

        MinecraftProtocol.send(player, metadataPacket)
    }

}
