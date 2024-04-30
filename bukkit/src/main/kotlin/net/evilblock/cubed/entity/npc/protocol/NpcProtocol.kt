/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.npc.protocol

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

object NpcProtocol
{
    private val ENUM_GAME_MODE_CLASS =
        MinecraftReflection.getNMSClass("WorldSettings\$EnumGamemode")
            ?: Reflection.getClassSuppressed(
                "net.minecraft.world.level.EnumGamemode"
            )!!

    private val I_CHAT_BASE_COMPONENT_CLASS =
        MinecraftReflection.getNMSClass("IChatBaseComponent")
            ?: Reflection.getClassSuppressed(
                "net.minecraft.network.chat.IChatBaseComponent"
            )!!

    private val PACKET_PLAY_OUT_PLAYER_INFO_CLASS =
        MinecraftProtocol.getPacketClass("PacketPlayOutPlayerInfo")

    private val CHAT_SERIALIZER_CLASS =
        MinecraftReflection.getNMSClass("IChatBaseComponent\$ChatSerializer")
            ?: I_CHAT_BASE_COMPONENT_CLASS.declaredClasses
                .find {
                    it.simpleName.equals("ChatSerializer", true)
                }!!

    private val JSON_CONVERSION_METHOD: Method =
        Reflection.getDeclaredMethod(CHAT_SERIALIZER_CLASS, "a", String::class.java)!!

    val ENUM_PLAYER_INFO_ACTION_CLASS =
        PACKET_PLAY_OUT_PLAYER_INFO_CLASS.declaredClasses
            .find {
                it.simpleName.equals("EnumPlayerInfoAction", true)
            }!!

    val PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR =
        Reflection.getConstructor(
            PACKET_PLAY_OUT_PLAYER_INFO_CLASS,
            ENUM_PLAYER_INFO_ACTION_CLASS,
            if (MinecraftReflection.isModern())
                Collection::class.java else Iterable::class.java
        )!!

    private val PLAYER_INFO_DATA_CLASS =
        PACKET_PLAY_OUT_PLAYER_INFO_CLASS.declaredClasses
            .find {
                it.simpleName.equals("PlayerInfoData", true)
            }
            ?: MinecraftReflection.getNMSClass(
                "PacketPlayOutPlayerInfo\$PlayerInfoData"
            )

    private val PLAYER_INFO_DATA_CONSTRUCTOR = PLAYER_INFO_DATA_CLASS
        ?.declaredConstructors?.first()
        ?: throw IllegalStateException(
            "Constructors not found for the data class"
        )

    val tropicSpigotEnabled = Reflection
        .getClassSuppressed(
            "dev.reximian9k.spigot.knockback.DefaultKnockbackProfileReference"
        ) != null

    private val NOT_SET_GAMEMODE =
        Reflection.getEnum(
            ENUM_GAME_MODE_CLASS,
            if (MinecraftReflection.isModern())
                "a" else "NOT_SET"
        )!!

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
            JSON_CONVERSION_METHOD
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

        return PLAYER_INFO_DATA_CONSTRUCTOR
            .newInstance(
                *arguments.toTypedArray()
            )
    }

}
