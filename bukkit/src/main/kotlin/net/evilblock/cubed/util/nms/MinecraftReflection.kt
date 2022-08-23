/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.nms

import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.ServerVersion
import net.evilblock.cubed.util.nms.wrapper.Vector3FWrapper
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*
import kotlin.math.floor

object MinecraftReflection
{
    private val GAME_PROFILE_CLASS: Class<*>? =
        Reflection.getClassSuppressed("com.mojang.authlib.GameProfile")

    private val GAME_PROFILE_CLASS_LEGACY: Class<*>? =
        Reflection.getClassSuppressed("com.mojang.authlib.GameProfile")

    private val PROPERTY_MAP_CLASS: Class<*>? =
        Reflection.getClassSuppressed("com.mojang.authlib.properties.PropertyMap")

    private val PROPERTY_MAP_CLASS_LEGACY: Class<*>? =
        Reflection.getClassSuppressed("com.mojang.authlib.properties.PropertyMap")

    private val PROPERTY_CLASS: Class<*>? = Reflection.getClassSuppressed("com.mojang.authlib.properties.Property")

    private val PROPERTY_CLASS_LEGACY: Class<*>? =
        Reflection.getClassSuppressed("com.mojang.authlib.properties.Property")

    private val PROPERTY_VALUE_FIELD: Field =
        Reflection.getDeclaredField((PROPERTY_CLASS ?: PROPERTY_CLASS_LEGACY)!!, "value")!!

    private val PROPERTY_SIGNATURE_FIELD: Field =
        Reflection.getDeclaredField((PROPERTY_CLASS ?: PROPERTY_CLASS_LEGACY)!!, "signature")!!

    private val PROPERTY_MAP_CONTAINS_KEY_METHOD: Method =
        Reflection.getMethodSuppressed(getPropertyMapClass(), "containsKey", Any::class.java)!!

    private val PROPERTY_MAP_GET_METHOD: Method =
        Reflection.getMethodSuppressed(getPropertyMapClass(), "get", Any::class.java)!!

    private val PROPERTY_MAP_PUT_METHOD: Method =
        Reflection.getMethodSuppressed(getPropertyMapClass(), "put", Any::class.java, Any::class.java)!!

    val MINECRAFT_SERVER_CLASS: Class<*> = getNMSClass("MinecraftServer")!!

    private val ENUM_PARTICLE_CLASS: Class<*>? =
        getNMSClass("EnumParticle")

    private val ENUM_PARTICLE_MODERN_CLASS: Class<*>? =
        Reflection.getClassSuppressed(
            "net.minecraft.core.particles.Particles"
        )

    private val ENTITY_HUMAN_CLASS: Class<*> = if (!isModern())
    {
        getNMSClass("EntityHuman")!!
    } else
    {
        Reflection.getClassSuppressed(
            "net.minecraft.world.entity.player.EntityHuman"
        )!!
    }

    val ENTITY_PLAYER_CLASS: Class<*> = if (!isModern())
    {
        getNMSClass("EntityPlayer")!!
    } else
    {
        Reflection.getClassSuppressed(
            "net.minecraft.server.level.EntityPlayer"
        )!!
    }

    val PLAYER_CONNECTION_CLASS: Class<*> = if (!isModern())
    {
        getNMSClass("EntityPlayer")!!
    } else
    {
        Reflection.getClassSuppressed(
            "net.minecraft.server.network.PlayerConnection"
        )!!
    }

    val PACKET_CLASS: Class<*> = if (!isModern())
    {
        getNMSClass("Packet")!!
    } else
    {
        Reflection.getClassSuppressed(
            "net.minecraft.network.protocol.Packet"
        )!!
    }

    private val ENTITY_HUMAN_GAME_PROFILE_FIELD: Field? =
        Reflection.getDeclaredField(ENTITY_HUMAN_CLASS, "bH")

    private val CRAFT_INVENTORY_CUSTOM_CLASS = if (!isModern())
    {
        getCraftBukkitClass("inventory.CraftInventoryCustom")!!
    } else
    {
        Reflection.getClassSuppressed("org.bukkit.craftbukkit.${
            ServerVersion.getVersion().name
        }.inventory.CraftInventoryCustom")!!
    }

    @JvmStatic
    val ARMOR_STAND_HEAD_POSE: Vector3FWrapper = Vector3FWrapper(0.0f, 0.0f, 0.0f)

    @JvmStatic
    val ARMOR_STAND_BODY_POSE: Vector3FWrapper = Vector3FWrapper(0.0f, 0.0f, 0.0f)

    @JvmStatic
    val ARMOR_STAND_LEFT_ARM_POSE: Vector3FWrapper = Vector3FWrapper(-10.0f, 0.0f, -10.0f)

    @JvmStatic
    val ARMOR_STAND_RIGHT_ARM_POSE: Vector3FWrapper = Vector3FWrapper(-15.0f, 0.0f, 10.0f)

    @JvmStatic
    val ARMOR_STAND_LEFT_LEG_POSE: Vector3FWrapper = Vector3FWrapper(-1.0f, 0.0f, -1.0f)

    @JvmStatic
    val ARMOR_STAND_RIGHT_LEG_POSE: Vector3FWrapper = Vector3FWrapper(1.0f, 0.0f, 1.0f)

    @JvmStatic
    fun getCraftBukkitVersion(): String
    {
        return "org.bukkit.craftbukkit.${getVersion()}."
    }

    @JvmStatic
    fun isModern(): Boolean
    {
        return ServerVersion
            .getVersion()
            .isNewerThan(
                ServerVersion.v1_16
            )
    }

    @JvmStatic
    fun getNmsVersion(): String
    {
        return "net.minecraft.server${
            if (isModern())
            {
                ""
            } else
            {
                ".${ServerVersion.getVersion().name}"
            }
        }."
    }

    @JvmStatic
    fun getVersion(): String
    {
        return ServerVersion.getVersion().name
    }

    @JvmStatic
    fun getServerVersion(): Int
    {
        return ServerVersion.getVersion().protocolVersion
    }

    @JvmStatic
    fun getMinecraftServer(): Any
    {
        return Reflection.callMethod(Bukkit.getServer(), "getServer")!!
    }

    @JvmStatic
    fun getTPS(): Double
    {
        return (Reflection.getFieldValue(getMinecraftServer(), "recentTps") as DoubleArray)[0]
    }

    @JvmStatic
    fun getPing(player: Player): Int
    {
        return Reflection.getDeclaredFieldValue(getHandle(player), "ping") as Int
    }

    @JvmStatic
    fun getHandle(entity: Entity): Any
    {
        return Reflection.callMethod(entity, "getHandle")!!
    }

    @JvmStatic
    fun getHandle(world: World): Any
    {
        return Reflection.callMethod(world, "getHandle")!!
    }

    @JvmStatic
    fun getHandle(block: Block): Any
    {
        return Reflection.callDeclaredMethod(block, "getNMSBlock")!!
    }

    @JvmStatic
    fun getNMSClass(name: String): Class<*>?
    {
        return Reflection.getClassSuppressed(getNmsVersion() + name)
    }

    @JvmStatic
    fun getCraftBukkitClass(name: String): Class<*>?
    {
        return Reflection.getClassSuppressed(
            "${getCraftBukkitVersion()}$name"
        )
    }

    @JvmStatic
    fun getGameProfileClass(): Class<*>
    {
        return GAME_PROFILE_CLASS ?: GAME_PROFILE_CLASS_LEGACY!!
    }

    @JvmStatic
    fun getPropertyMapClass(): Class<*>
    {
        return PROPERTY_MAP_CLASS ?: PROPERTY_MAP_CLASS_LEGACY!!
    }

    @JvmStatic
    fun getPropertyClass(): Class<*>
    {
        return PROPERTY_CLASS ?: PROPERTY_CLASS_LEGACY!!
    }

    @JvmStatic
    fun newGameProfile(uuid: UUID, name: String): Any
    {
        return Reflection.callConstructor(getGameProfileClass(), uuid, name)!!
    }

    @JvmStatic
    fun getGameProfile(player: Player): Any
    {
        return ENTITY_HUMAN_GAME_PROFILE_FIELD!!.get(getHandle(player))
    }

    @JvmStatic
    fun setGameProfileProperty(profile: Any, propertyName: String, property: Any)
    {
        val properties = Reflection.getDeclaredFieldValue(profile, "properties")!!
        PROPERTY_MAP_PUT_METHOD.invoke(properties, propertyName, property)
    }

    @JvmStatic
    fun getGameProfileProperty(profile: Any, propertyName: String): Any
    {
        val properties = Reflection.getDeclaredFieldValue(profile, "properties")!!
        return (PROPERTY_MAP_GET_METHOD.invoke(properties, propertyName) as Collection<*>).iterator().next()!!
    }

    @JvmStatic
    fun getGameProfileTexture(profile: Any): Pair<String, String>
    {
        val property = getGameProfileProperty(profile, "textures")
        return Pair(PROPERTY_VALUE_FIELD.get(property) as String, PROPERTY_SIGNATURE_FIELD.get(property) as String)
    }

    @JvmStatic
    fun setGameProfileTexture(profile: Any, textureValue: String, textureSignature: String)
    {
        val property =
            Reflection.callDeclaredConstructor(getPropertyClass(), "textures", textureValue, textureSignature)!!
        setGameProfileProperty(profile, "textures", property)
    }

    @JvmStatic
    fun getGameProfileUUID(profile: Any): UUID
    {
        return Reflection.getDeclaredFieldValue(profile, "id")!! as UUID
    }

    @JvmStatic
    fun getGameProfileName(profile: Any): String
    {
        return Reflection.getDeclaredFieldValue(profile, "name")!! as String
    }

    @JvmStatic
    fun getEnumParticle(name: String): Any?
    {
        if (ENUM_PARTICLE_CLASS != null)
        {
            return Reflection
                .getEnum(ENUM_PARTICLE_CLASS, name)
        }

        if (ENUM_PARTICLE_MODERN_CLASS != null)
        {
            return Reflection.getEnum(
                ENUM_PARTICLE_MODERN_CLASS, name
            )
        }

        return null
    }

    @JvmStatic
    fun mathFloor(value: Double): Int
    {
        return floor(value).toInt()
    }

    @JvmStatic
    fun isCustomInventory(inventory: Inventory): Boolean
    {
        return inventory.javaClass == CRAFT_INVENTORY_CUSTOM_CLASS
    }

}
