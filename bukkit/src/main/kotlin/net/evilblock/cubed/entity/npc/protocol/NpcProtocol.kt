

package net.evilblock.cubed.entity.npc.protocol

import gg.scala.commons.tablist.util.Util
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.ServerVersion
import net.evilblock.cubed.util.bukkit.AngleUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.nms.DataWatcherUtil
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.cubed.util.nms.MinecraftReflection
import org.bukkit.entity.Player
import java.lang.reflect.Method
import java.util.*
import java.util.Optional

object NpcProtocol
{
    private val ENUM_GAME_MODE_CLASS =
        MinecraftReflection.getNMSClass("WorldSettings\$EnumGamemode")
            ?: Reflection.getClassSuppressed(
                "net.minecraft.world.level.EnumGamemode"
            )!!

    private val I_CHAT_BASE_COMPONENT_CLASS =
        if (!MinecraftReflection.isModern())
            MinecraftReflection.getNMSClass("IChatBaseComponent")
                ?: Reflection.getClassSuppressed(
                    "net.minecraft.network.chat.IChatBaseComponent"
                )
        else null

    private val PACKET_PLAY_OUT_PLAYER_INFO_CLASS =
        if (!MinecraftReflection.isModern()) MinecraftProtocol.getPacketClass("PacketPlayOutPlayerInfo")
        else null

    private val CHAT_SERIALIZER_CLASS =
        if (!MinecraftReflection.isModern())
            MinecraftReflection.getNMSClass("IChatBaseComponent\$ChatSerializer")
                ?: I_CHAT_BASE_COMPONENT_CLASS?.declaredClasses?.find {
                    it.simpleName.equals("ChatSerializer", true)
                }
        else null

    private val JSON_CONVERSION_METHOD: Method? =
        if (CHAT_SERIALIZER_CLASS != null)
            Reflection.getDeclaredMethod(CHAT_SERIALIZER_CLASS, "a", String::class.java)
        else null

    val ENUM_PLAYER_INFO_ACTION_CLASS =
        PACKET_PLAY_OUT_PLAYER_INFO_CLASS?.declaredClasses?.find {
            it.simpleName.equals("EnumPlayerInfoAction", true)
        }

    val PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR =
        if (PACKET_PLAY_OUT_PLAYER_INFO_CLASS != null && ENUM_PLAYER_INFO_ACTION_CLASS != null)
            Reflection.getConstructor(
                PACKET_PLAY_OUT_PLAYER_INFO_CLASS,
                ENUM_PLAYER_INFO_ACTION_CLASS,
                Iterable::class.java
            )
        else null

    private val PLAYER_INFO_DATA_CLASS =
        PACKET_PLAY_OUT_PLAYER_INFO_CLASS?.declaredClasses?.find {
            it.simpleName.equals("PlayerInfoData", true)
        }
            ?: MinecraftReflection.getNMSClass(
                "PacketPlayOutPlayerInfo\$PlayerInfoData"
            )

    private val PLAYER_INFO_DATA_CONSTRUCTOR = PLAYER_INFO_DATA_CLASS?.declaredConstructors?.first()

    val tropicSpigotEnabled = Reflection
        .getClassSuppressed(
            "dev.reximian9k.spigot.knockback.DefaultKnockbackProfileReference"
        ) != null

    private val NOT_SET_GAMEMODE =
        Reflection.getEnum(
            ENUM_GAME_MODE_CLASS,
            if (MinecraftReflection.isModern())
                "a" else "NOT_SET"
        )

    private fun createPlayerInfoData(
        playerInfoPacket: Any,
        gameProfile: Any
    ): Any
    {
        val arguments = mutableListOf<Any?>()

        if (!MinecraftReflection.isModern())
        {
            if (!tropicSpigotEnabled)
            {
                arguments.add(playerInfoPacket)
            }
        }

        arguments.add(gameProfile)
        arguments.add(0)
        arguments.add(NOT_SET_GAMEMODE)
        arguments.add(
            JSON_CONVERSION_METHOD!!
                .invoke(
                    null,
                    "{\"text\":\"[NPC] ${
                        UUID.randomUUID().toString().substring(0, 8)
                    }\",\"color\":\"dark_gray\"}"
                )
        )

        if (
            ServerVersion.getVersion()
                .isNewerThanOrEquals(ServerVersion.v1_19)
        )
        {
            arguments.add(null)
        }

        return PLAYER_INFO_DATA_CONSTRUCTOR!!
            .newInstance(
                *arguments.toTypedArray()
            )
    }

    fun createDataWatcher(npc: NpcEntity): Any
    {
        if (MinecraftReflection.isModern())
        {
            return createModernMetadata(npc)
        }

        val dataWatcher = DataWatcherUtil.new()

        DataWatcherUtil.register(dataWatcher, 0, 0.toByte())
        DataWatcherUtil.register(dataWatcher, 2, npc.nametag.name)
        DataWatcherUtil.register(dataWatcher, 6, 20.0F)
        DataWatcherUtil.register(dataWatcher, 7, 0)
        DataWatcherUtil.register(dataWatcher, 8, 0.toByte())
        DataWatcherUtil.register(dataWatcher, 9, 0.toByte())
        DataWatcherUtil.register(dataWatcher, 10, 127.toByte())

        DataWatcherUtil.register(dataWatcher, 17, 0.0F)
        DataWatcherUtil.register(dataWatcher, 18, 1)

        return dataWatcher
    }

    fun sendSpawnPackets(player: Player, npc: NpcEntity)
    {
        if (MinecraftReflection.isModern())
        {
            sendSpawnPacketsModern(player, npc)
            return
        }

        val spawnProfile = if (npc.personalizedSkin)
        {
            runCatching {
                val liveProfile = Util.getPlayerProfile(player)
                val skinData = liveProfile.skinData
                if (skinData != null)
                {
                    MinecraftReflection.newGameProfile(
                        npc.uuid,
                        liveProfile.name,
                        skinData.value,
                        skinData.signature ?: ""
                    )
                } else
                {
                    MinecraftReflection.newGameProfile(npc.uuid, liveProfile.name)
                }
            }.getOrElse {
                val personalProfile = MinecraftReflection.newGameProfile(
                    npc.uuid,
                    MinecraftReflection.getGameProfileName(npc.profile)
                )

                runCatching {
                    val playerProfile = MinecraftReflection.getGameProfile(player)
                    val (value, signature) = MinecraftReflection.getGameProfileTexture(playerProfile)
                    MinecraftReflection.newGameProfile(
                        npc.uuid,
                        MinecraftReflection.getGameProfileName(npc.profile),
                        value,
                        signature
                    )
                }.getOrDefault(personalProfile)
            }
        } else
        {
            npc.profile
        }

        val playerInfoAddPacket =
            MinecraftProtocol.newPacket("PacketPlayOutPlayerInfo")

        Reflection.setDeclaredFieldValue(
            playerInfoAddPacket,
            "a",
            Reflection.getEnum(ENUM_PLAYER_INFO_ACTION_CLASS!!, "ADD_PLAYER")!!
        )

        Reflection.setDeclaredFieldValue(
            playerInfoAddPacket,
            "b",
            arrayListOf(createPlayerInfoData(playerInfoAddPacket, spawnProfile))
        )

        val playerInfoRemovePacket = MinecraftProtocol
            .newPacket("PacketPlayOutPlayerInfo")

        Reflection.setDeclaredFieldValue(
            playerInfoRemovePacket,
            "a",
            Reflection.getEnum(ENUM_PLAYER_INFO_ACTION_CLASS!!, "REMOVE_PLAYER")!!
        )

        Reflection.setDeclaredFieldValue(
            playerInfoRemovePacket,
            "b",
            arrayListOf(createPlayerInfoData(playerInfoRemovePacket, spawnProfile))
        )

        val entitySpawnPacket = MinecraftProtocol
            .newPacket("PacketPlayOutNamedEntitySpawn")

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
        Reflection.setDeclaredFieldValue(
            entityTeleportPacket,
            "b",
            MinecraftReflection.mathFloor(npc.location.x * 32.0)
        )
        Reflection.setDeclaredFieldValue(
            entityTeleportPacket,
            "c",
            MinecraftReflection.mathFloor(npc.location.y * 32.0)
        )
        Reflection.setDeclaredFieldValue(
            entityTeleportPacket,
            "d",
            MinecraftReflection.mathFloor(npc.location.z * 32.0)
        )
        Reflection.setDeclaredFieldValue(entityTeleportPacket, "e", AngleUtils.yawToBytes(npc.location.yaw))
        Reflection.setDeclaredFieldValue(entityTeleportPacket, "f", AngleUtils.yawToBytes(npc.location.pitch))
        Reflection.setDeclaredFieldValue(entityTeleportPacket, "g", true)

        val animationPacket = MinecraftProtocol.newPacket("PacketPlayOutAnimation")
        Reflection.setDeclaredFieldValue(animationPacket, "a", npc.id)
        Reflection.setDeclaredFieldValue(animationPacket, "b", 0)

        MinecraftProtocol.send(player, playerInfoAddPacket) 
        npc.nametag.enforceSendSync(player, "NPC_${npc.id}") 

        Tasks.asyncDelayed(1L) {
            MinecraftProtocol.send(player, entitySpawnPacket) 

            if (npc.itemInHand != null)
            {
                runCatching {
                    MinecraftProtocol.send(
                        player,
                        MinecraftProtocol.buildEntityEquipmentPacket(npc.id, 0, npc.itemInHand!!)
                    )
                }
            }

            if (npc.helmet != null)
            {
                MinecraftProtocol.send(player, MinecraftProtocol.buildEntityEquipmentPacket(npc.id, 4, npc.helmet!!))
            }

            if (npc.chestplate != null)
            {
                MinecraftProtocol.send(
                    player,
                    MinecraftProtocol.buildEntityEquipmentPacket(npc.id, 3, npc.chestplate!!)
                )
            }

            if (npc.leggings != null)
            {
                MinecraftProtocol.send(player, MinecraftProtocol.buildEntityEquipmentPacket(npc.id, 2, npc.leggings!!))
            }

            if (npc.boots != null)
            {
                MinecraftProtocol.send(player, MinecraftProtocol.buildEntityEquipmentPacket(npc.id, 1, npc.boots!!))
            }

            Tasks.asyncDelayed(4L) {
                MinecraftProtocol.send(player, entityLookPacket)
                MinecraftProtocol.send(player, entityHeadRotationPacket)
                MinecraftProtocol.send(player, entityTeleportPacket)
                MinecraftProtocol.send(player, animationPacket)
            }
        }

        
        Tasks.asyncDelayed(40L) {
            MinecraftProtocol.send(player, playerInfoRemovePacket) 
        }
    }

    fun sendUpdatePackets(player: Player, npc: NpcEntity)
    {
        if (MinecraftReflection.isModern())
        {
            MinecraftProtocol.send(player, MinecraftProtocol.buildModernEntityMetadataPacket(npc.id, npc.metadata as List<Any>))
            return
        }

        val entityMetadataPacket = MinecraftProtocol.newPacket("PacketPlayOutEntityMetadata")
        Reflection.setDeclaredFieldValue(entityMetadataPacket, "a", npc.id)
        Reflection.setDeclaredFieldValue(entityMetadataPacket, "b", DataWatcherUtil.getWatchedObjects(npc.metadata))

        MinecraftProtocol.send(player, entityMetadataPacket)
    }

    private fun createModernMetadata(npc: NpcEntity): List<Any>
    {
        return listOf(
            MinecraftProtocol.createEntityDataValue(
                "net.minecraft.world.entity.Entity",
                "DATA_SHARED_FLAGS_ID",
                0.toByte()
            ),
            MinecraftProtocol.createEntityDataValue(
                "net.minecraft.world.entity.Entity",
                "DATA_CUSTOM_NAME",
                Optional.of(MinecraftProtocol.createLiteralComponent(npc.nametag.name))
            ),
            MinecraftProtocol.createEntityDataValue(
                "net.minecraft.world.entity.Entity",
                "DATA_CUSTOM_NAME_VISIBLE",
                false
            ),
            MinecraftProtocol.createEntityDataValue(
                "net.minecraft.world.entity.Entity",
                "DATA_NO_GRAVITY",
                true
            ),
            MinecraftProtocol.createEntityDataValue(
                "net.minecraft.world.entity.LivingEntity",
                "DATA_HEALTH_ID",
                20.0f
            ),
            MinecraftProtocol.createEntityDataValue(
                "net.minecraft.world.entity.player.Player",
                "DATA_PLAYER_ABSORPTION_ID",
                0.0f
            ),
            MinecraftProtocol.createEntityDataValue(
                "net.minecraft.world.entity.player.Player",
                "DATA_SCORE_ID",
                0
            )
        )
    }

    private fun sendSpawnPacketsModern(player: Player, npc: NpcEntity)
    {
        val spawnProfile = if (npc.personalizedSkin)
        {
            runCatching {
                val liveProfile = Util.getPlayerProfile(player)
                val skinData = liveProfile.skinData
                if (skinData != null)
                {
                    MinecraftReflection.newGameProfile(
                        npc.uuid,
                        liveProfile.name,
                        skinData.value,
                        skinData.signature ?: ""
                    )
                } else
                {
                    MinecraftReflection.newGameProfile(npc.uuid, liveProfile.name)
                }
            }.getOrElse {
                val personalProfile = MinecraftReflection.newGameProfile(
                    npc.uuid,
                    MinecraftReflection.getGameProfileName(npc.profile)
                )

                runCatching {
                    val playerProfile = MinecraftReflection.getGameProfile(player)
                    val (value, signature) = MinecraftReflection.getGameProfileTexture(playerProfile)
                    MinecraftReflection.newGameProfile(
                        npc.uuid,
                        MinecraftReflection.getGameProfileName(npc.profile),
                        value,
                        signature
                    )
                }.getOrDefault(personalProfile)
            }
        } else
        {
            npc.profile
        }

        val actionClass = Reflection.getClassSuppressed(
            "net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket\$Action"
        )!!
        val addPlayer = Reflection.getEnum(actionClass, "ADD_PLAYER") as Enum<*>
        val updateListed = Reflection.getEnum(actionClass, "UPDATE_LISTED") as Enum<*>
        val updateLatency = Reflection.getEnum(actionClass, "UPDATE_LATENCY") as Enum<*>
        val updateGameMode = Reflection.getEnum(actionClass, "UPDATE_GAME_MODE") as Enum<*>
        val updateDisplayName = Reflection.getEnum(actionClass, "UPDATE_DISPLAY_NAME") as Enum<*>
        val updateHat = Reflection.getEnum(actionClass, "UPDATE_HAT") as Enum<*>
        val updateListOrder = Reflection.getEnum(actionClass, "UPDATE_LIST_ORDER") as Enum<*>

        @Suppress("UNCHECKED_CAST")
        val actions = EnumSet.noneOf(actionClass as Class<out Enum<*>>) as EnumSet<Enum<*>>
        actions.add(addPlayer)
        actions.add(updateListed)
        actions.add(updateLatency)
        actions.add(updateGameMode)
        actions.add(updateDisplayName)
        actions.add(updateHat)
        actions.add(updateListOrder)

        val entryClass = Reflection.getClassSuppressed(
            "net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket\$Entry"
        )!!
        val gameTypeClass = Reflection.getClassSuppressed("net.minecraft.world.level.GameType")!!
        val defaultMode = Reflection.getDeclaredField(gameTypeClass, "DEFAULT_MODE")!!.get(null)
        val entryConstructor = entryClass.declaredConstructors.first { it.parameterTypes.size == 9 }
        entryConstructor.isAccessible = true
        val entry = entryConstructor.newInstance(
            npc.uuid,
            spawnProfile,
            true,
            0,
            defaultMode,
            null,
            true,
            0,
            null
        )

        val infoUpdatePacketClass = Reflection.getClassSuppressed(
            "net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket"
        )!!
        val infoUpdateConstructor = infoUpdatePacketClass.declaredConstructors.first {
            it.parameterTypes.size == 2 && it.parameterTypes[1] == List::class.java
        }
        infoUpdateConstructor.isAccessible = true
        val playerInfoAddPacket = infoUpdateConstructor.newInstance(actions, listOf(entry))

        val removePacketClass = Reflection.getClassSuppressed(
            "net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket"
        )!!
        val removeConstructor = removePacketClass.declaredConstructors.first {
            it.parameterTypes.size == 1 && it.parameterTypes[0] == List::class.java
        }
        removeConstructor.isAccessible = true
        val playerInfoRemovePacket = removeConstructor.newInstance(listOf(npc.uuid))

        val entitySpawnPacket = MinecraftProtocol.buildModernAddEntityPacket(npc.id, npc.uuid, "PLAYER", npc.location)
        val entityLookPacket = MinecraftProtocol.buildEntityLookPacket(npc.id, npc.location.yaw, npc.location.pitch)
        val entityHeadRotationPacket = MinecraftProtocol.buildEntityHeadRotationPacket(npc.id, npc.location.yaw)
        val entityTeleportPacket = MinecraftProtocol.buildEntityTeleportPacket(npc.id, npc.location)
        val metadataPacket = MinecraftProtocol.buildModernEntityMetadataPacket(npc.id, npc.metadata as List<Any>)

        MinecraftProtocol.send(player, playerInfoAddPacket)
        npc.nametag.enforceSendSync(player, "NPC_${npc.id}")

        Tasks.asyncDelayed(1L) {
            MinecraftProtocol.send(player, entitySpawnPacket, metadataPacket)

            if (npc.itemInHand != null)
            {
                runCatching {
                    MinecraftProtocol.send(player, MinecraftProtocol.buildEntityEquipmentPacket(npc.id, 0, npc.itemInHand!!))
                }
            }

            if (npc.helmet != null)
            {
                MinecraftProtocol.send(player, MinecraftProtocol.buildEntityEquipmentPacket(npc.id, 4, npc.helmet!!))
            }

            if (npc.chestplate != null)
            {
                MinecraftProtocol.send(player, MinecraftProtocol.buildEntityEquipmentPacket(npc.id, 3, npc.chestplate!!))
            }

            if (npc.leggings != null)
            {
                MinecraftProtocol.send(player, MinecraftProtocol.buildEntityEquipmentPacket(npc.id, 2, npc.leggings!!))
            }

            if (npc.boots != null)
            {
                MinecraftProtocol.send(player, MinecraftProtocol.buildEntityEquipmentPacket(npc.id, 1, npc.boots!!))
            }

            Tasks.asyncDelayed(4L) {
                MinecraftProtocol.send(player, entityLookPacket, entityHeadRotationPacket, entityTeleportPacket)
            }
        }

        Tasks.asyncDelayed(40L) {
            MinecraftProtocol.send(player, playerInfoRemovePacket)
        }
    }

}
