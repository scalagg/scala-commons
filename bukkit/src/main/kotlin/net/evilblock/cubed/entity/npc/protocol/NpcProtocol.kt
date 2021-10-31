/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.npc.protocol

import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.AngleUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.nms.DataWatcherUtil
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.cubed.util.nms.MinecraftReflection
import org.bukkit.entity.Player
import java.lang.reflect.Constructor

object NpcProtocol {

    private val NBT_TAG_COMPOUND_CLASS: Class<*> = MinecraftReflection.getNMSClass("NBTTagCompound")!!

    private val ENUM_GAME_MODE_CLASS: Class<*> = MinecraftReflection.getNMSClass("WorldSettings\$EnumGamemode")!!
//    private val ENUM_GAME_MODE_CLASS: Class<*> = MinecraftReflection.getNMSClass("EnumGamemode")!! // 1.12 class
    private val I_CHAT_BASE_COMPONENT_CLASS: Class<*> = MinecraftReflection.getNMSClass("IChatBaseComponent")!!

    val PACKET_PLAY_OUT_PLAYER_INFO_CLASS: Class<*> = MinecraftReflection.getNMSClass("PacketPlayOutPlayerInfo")!!
    // PacketPlayOutPlayerInfo.EnumPlayerInfoAction var1, Iterable<EntityPlayer> var2

    val ENUM_PLAYER_INFO_ACTION_CLASS: Class<*> = MinecraftReflection.getNMSClass("PacketPlayOutPlayerInfo\$EnumPlayerInfoAction")!!
    val PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR: Constructor<*> = Reflection.getConstructor(PACKET_PLAY_OUT_PLAYER_INFO_CLASS, ENUM_PLAYER_INFO_ACTION_CLASS, Iterable::class.java)!!

    private val PLAYER_INFO_DATA_CLASS: Class<*> = PACKET_PLAY_OUT_PLAYER_INFO_CLASS.declaredClasses.find { it.simpleName == "PlayerInfoData" }!!
    private val PLAYER_INFO_DATA_CONSTRUCTOR: Constructor<*> = Reflection.getDeclaredConstructor(PLAYER_INFO_DATA_CLASS, PACKET_PLAY_OUT_PLAYER_INFO_CLASS, MinecraftReflection.getGameProfileClass(), Int::class.java, ENUM_GAME_MODE_CLASS, I_CHAT_BASE_COMPONENT_CLASS)!!

    private val ENTITY_CLASS = MinecraftReflection.getNMSClass("Entity")!!
    private val ENTITY_LIVING_CLASS = MinecraftReflection.getNMSClass("EntityLiving")!!
    private val ENTITY_HUMAN_CLASS = MinecraftReflection.getNMSClass("EntityHuman")!!

    fun createPlayerInfoData(playerInfoPacket: Any, gameProfile: Any): Any {
        val enumGameMode = Reflection.getEnum(ENUM_GAME_MODE_CLASS, "SURVIVAL")!!
        return PLAYER_INFO_DATA_CONSTRUCTOR.newInstance(playerInfoPacket, gameProfile, 0, enumGameMode, null)
    }

    fun createDataWatcher(npc: NpcEntity): Any {
        val dataWatcher = DataWatcherUtil.new()

        DataWatcherUtil.register(dataWatcher, 0, 0.toByte())
        DataWatcherUtil.register(dataWatcher, 2, npc.nametag.name)
        DataWatcherUtil.register(dataWatcher, 6, 20.0F)
        DataWatcherUtil.register(dataWatcher, 7, 0)
        DataWatcherUtil.register(dataWatcher, 8, 0.toByte())
        DataWatcherUtil.register(dataWatcher, 9, 0.toByte())
        DataWatcherUtil.register(dataWatcher, 17, 0.0F)
        DataWatcherUtil.register(dataWatcher, 18, 0)

        return dataWatcher
    }

    fun sendSpawnPackets(player: Player, npc: NpcEntity) {
        val playerInfoAddPacket = MinecraftProtocol.newPacket("PacketPlayOutPlayerInfo")
        Reflection.setDeclaredFieldValue(playerInfoAddPacket, "a", Reflection.getEnum(ENUM_PLAYER_INFO_ACTION_CLASS, "ADD_PLAYER")!!)
        Reflection.setDeclaredFieldValue(playerInfoAddPacket, "b", arrayListOf(createPlayerInfoData(playerInfoAddPacket, npc.profile)))

        val playerInfoRemovePacket = MinecraftProtocol.newPacket("PacketPlayOutPlayerInfo")
        Reflection.setDeclaredFieldValue(playerInfoRemovePacket, "a", Reflection.getEnum(ENUM_PLAYER_INFO_ACTION_CLASS, "REMOVE_PLAYER")!!)
        Reflection.setDeclaredFieldValue(playerInfoRemovePacket, "b", arrayListOf(createPlayerInfoData(playerInfoRemovePacket, npc.profile)))

        val entitySpawnPacket = MinecraftProtocol.newPacket("PacketPlayOutNamedEntitySpawn")
        Reflection.setDeclaredFieldValue(entitySpawnPacket, "a", npc.id)
        Reflection.setDeclaredFieldValue(entitySpawnPacket, "b", npc.uuid)
        Reflection.setDeclaredFieldValue(entitySpawnPacket, "c", MinecraftReflection.mathFloor(npc.location.x * 32.0))
        Reflection.setDeclaredFieldValue(entitySpawnPacket, "d", MinecraftReflection.mathFloor(npc.location.y * 32.0))
        Reflection.setDeclaredFieldValue(entitySpawnPacket, "e", MinecraftReflection.mathFloor(npc.location.z * 32.0))
        Reflection.setDeclaredFieldValue(entitySpawnPacket, "f", AngleUtils.yawToBytes(npc.location.yaw))
        Reflection.setDeclaredFieldValue(entitySpawnPacket, "g", AngleUtils.yawToBytes(npc.location.pitch))
        Reflection.setDeclaredFieldValue(entitySpawnPacket, "h", 0)
        Reflection.setDeclaredFieldValue(entitySpawnPacket, "i", npc.metadata)

        val entityLookPacket = MinecraftProtocol.buildEntityLookPacket(npc.id, npc.location.yaw, npc.location.pitch)

        val entityHeadRotationPacket = MinecraftProtocol.newPacket("PacketPlayOutEntityHeadRotation")
        Reflection.setDeclaredFieldValue(entityHeadRotationPacket, "a", npc.id)
        Reflection.setDeclaredFieldValue(entityHeadRotationPacket, "b", AngleUtils.yawToBytes(npc.location.yaw))

        val entityTeleportPacket = MinecraftProtocol.newPacket("PacketPlayOutEntityTeleport")
        Reflection.setDeclaredFieldValue(entityTeleportPacket, "a", npc.id)
        Reflection.setDeclaredFieldValue(entityTeleportPacket, "b", MinecraftReflection.mathFloor(npc.location.x * 32.0))
        Reflection.setDeclaredFieldValue(entityTeleportPacket, "c", MinecraftReflection.mathFloor(npc.location.y * 32.0))
        Reflection.setDeclaredFieldValue(entityTeleportPacket, "d", MinecraftReflection.mathFloor(npc.location.z * 32.0))
        Reflection.setDeclaredFieldValue(entityTeleportPacket, "e", AngleUtils.yawToBytes(npc.location.yaw))
        Reflection.setDeclaredFieldValue(entityTeleportPacket, "f", AngleUtils.yawToBytes(npc.location.pitch))
        Reflection.setDeclaredFieldValue(entityTeleportPacket, "g", true)

        val animationPacket = MinecraftProtocol.newPacket("PacketPlayOutAnimation")
        Reflection.setDeclaredFieldValue(animationPacket, "a", npc.id)
        Reflection.setDeclaredFieldValue(animationPacket, "b", 0)

        MinecraftProtocol.send(player, playerInfoAddPacket) // send info ADD packet
        npc.nametag.enforceSendSync(player, "NPC_${npc.id}") // add player to team

        Tasks.asyncDelayed(1L) {
            MinecraftProtocol.send(player, entitySpawnPacket) // spawn entity

            // send equipment updates
            if (npc.itemInHand != null) {
                MinecraftProtocol.send(player, MinecraftProtocol.buildEntityEquipmentPacket(npc.id, 0, npc.itemInHand!!))
            }

            if (npc.helmet != null) {
                MinecraftProtocol.send(player, MinecraftProtocol.buildEntityEquipmentPacket(npc.id, 4, npc.helmet!!))
            }

            if (npc.chestplate != null) {
                MinecraftProtocol.send(player, MinecraftProtocol.buildEntityEquipmentPacket(npc.id, 3, npc.chestplate!!))
            }

            if (npc.leggings != null) {
                MinecraftProtocol.send(player, MinecraftProtocol.buildEntityEquipmentPacket(npc.id, 2, npc.leggings!!))
            }

            if (npc.boots != null) {
                MinecraftProtocol.send(player, MinecraftProtocol.buildEntityEquipmentPacket(npc.id, 1, npc.boots!!))
            }

            // send entity updates 4 ticks after
            Tasks.asyncDelayed(4L) {
                MinecraftProtocol.send(player, entityLookPacket)
                MinecraftProtocol.send(player, entityHeadRotationPacket)
                MinecraftProtocol.send(player, entityTeleportPacket)
                MinecraftProtocol.send(player, animationPacket)
            }
        }

        Tasks.asyncDelayed(30L) {
            MinecraftProtocol.send(player, playerInfoRemovePacket) // send info REMOVE packet
        }
    }

    fun sendUpdatePackets(player: Player, npc: NpcEntity) {
        val entityMetadataPacket = MinecraftProtocol.newPacket("PacketPlayOutEntityMetadata")
        Reflection.setDeclaredFieldValue(entityMetadataPacket, "a", npc.id)
        Reflection.setDeclaredFieldValue(entityMetadataPacket, "b", DataWatcherUtil.getWatchedObjects(npc.metadata))

        MinecraftProtocol.send(player, entityMetadataPacket)
    }

}
