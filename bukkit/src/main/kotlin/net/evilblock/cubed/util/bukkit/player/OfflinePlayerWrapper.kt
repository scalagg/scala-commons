package net.evilblock.cubed.util.bukkit.player

import com.mojang.authlib.GameProfile
import gg.scala.cache.uuid.ScalaStoreUuidCache
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.nms.MinecraftReflection
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class OfflinePlayerWrapper(val source: String)
{
    var uniqueId: UUID? = null
    var name: String? = null

    fun loadAsync(callback: (Player?) -> Unit)
    {
        Tasks.async {
            val player = loadSync()

            Tasks.sync {
                callback.invoke(player)
            }
        }
    }

    fun loadSync(): Player?
    {
        val source = source.replace("'", "").replace("\"", "")
        uniqueId = ScalaStoreUuidCache.uniqueId(source)

        if (uniqueId == null)
        {
            name = this.source
            return null
        }

        name = ScalaStoreUuidCache.username(uniqueId!!)

        if (Bukkit.getPlayer(uniqueId) != null)
        {
            return Bukkit.getPlayer(uniqueId)
        }

        if (!Bukkit.getOfflinePlayer(uniqueId).hasPlayedBefore())
        {
            return null
        }

        if (ENTITY_PLAYER_CONSTRUCTOR == null)
            return null

        val server = MinecraftReflection.getMinecraftServer()
        val worldServer = MinecraftReflection.getWorldServers()[0]
        val gameProfile = MinecraftReflection.newGameProfile(uniqueId!!, name!!)

        val entityPlayer =
            if (!MinecraftReflection.isModern())
            {
                val playerInteractManager =
                    PLAYER_INTERACT_MANAGER_CONSTRUCTOR.newInstance(worldServer)

                ENTITY_PLAYER_CONSTRUCTOR.newInstance(
                    server, worldServer, gameProfile, playerInteractManager
                )
            } else
            {
                ENTITY_PLAYER_CONSTRUCTOR.newInstance(
                    server, worldServer, gameProfile
                )
            }

        val player = Reflection.callDeclaredMethod(entityPlayer, "getBukkitEntity")!!

        Reflection.callDeclaredMethod(player, "loadData")

        return player as Player
    }

    companion object
    {
        private val MINECRAFT_SERVER_CLASS =
            MinecraftReflection.MINECRAFT_SERVER_CLASS

        private val WORLD_CLASS =
            MinecraftReflection.getNMSClass("World")
                ?: Reflection.getClassSuppressed(
                    "net.minecraft.world.level.World"
                )!!

        private val WORLD_SERVER_CLASS =
            MinecraftReflection.getNMSClass("WorldServer")
                ?: Reflection.getClassSuppressed(
                    "net.minecraft.server.level.WorldServer"
                )!!

        private val PLAYER_INTERACT_MANAGER_CLASS =
            MinecraftReflection.getNMSClass("PlayerInteractManager")
                ?: Reflection.getClassSuppressed(
                    "net.minecraft.server.level.PlayerInteractManager"
                )!!

        private val ENTITY_PLAYER_CLASS =
            MinecraftReflection.ENTITY_PLAYER_CLASS

        private val ENTITY_PLAYER_CONSTRUCTOR =
            Reflection.getDeclaredConstructor(
                ENTITY_PLAYER_CLASS,
                MINECRAFT_SERVER_CLASS,
                WORLD_SERVER_CLASS,
                MinecraftReflection.getGameProfileClass(),
                PLAYER_INTERACT_MANAGER_CLASS
            ) ?: Reflection.getDeclaredConstructor(
                ENTITY_PLAYER_CLASS,
                MINECRAFT_SERVER_CLASS,
                WORLD_SERVER_CLASS,
                GameProfile::class.java
            )

        private val PLAYER_INTERACT_MANAGER_CONSTRUCTOR =
            Reflection.getDeclaredConstructor(
                PLAYER_INTERACT_MANAGER_CLASS,
                if (MinecraftReflection.isModern())
                    ENTITY_PLAYER_CLASS else WORLD_CLASS
            )!!
    }
}
