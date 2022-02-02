package net.evilblock.cubed.util.bukkit.player

import gg.scala.cache.uuid.ScalaStoreUuidCache
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.nms.MinecraftReflection
import java.util.UUID
import org.bukkit.entity.Player
import org.bukkit.Bukkit
import java.lang.reflect.Constructor

class OfflinePlayerWrapper(val source: String) {

    var uniqueId: UUID? = null
    var name: String? = null

    fun loadAsync(callback: (Player?) -> Unit) {
        Tasks.async {
            val player = loadSync()

            Tasks.sync {
                callback.invoke(player)
            }
        }
    }

    fun loadSync(): Player? {
        val source = source.replace("'", "").replace("\"", "")
        uniqueId = ScalaStoreUuidCache.uniqueId(source)

        if (uniqueId == null) {
            name = this.source
            return null
        }

        name = ScalaStoreUuidCache.username(uniqueId!!)

        if (Bukkit.getPlayer(uniqueId) != null) {
            return Bukkit.getPlayer(uniqueId)
        }

        if (!Bukkit.getOfflinePlayer(uniqueId).hasPlayedBefore()) {
            return null
        }

        val server = MinecraftReflection.getMinecraftServer()
        val worldServer = MinecraftReflection.getWorldServers()[0]
        val gameProfile = MinecraftReflection.newGameProfile(uniqueId!!, name!!)
        val playerInteractManager = PLAYER_INTERACT_MANAGER_CONSTRUCTOR.newInstance(worldServer)

        val entityPlayer = ENTITY_PLAYER_CONSTRUCTOR.newInstance(server, worldServer, gameProfile, playerInteractManager)
        val player = Reflection.callDeclaredMethod(entityPlayer, "getBukkitEntity")!!

        Reflection.callDeclaredMethod(player, "loadData")

        return player as Player
    }

    companion object {
        private val MINECRAFT_SERVER_CLASS: Class<*> = MinecraftReflection.getNMSClass("MinecraftServer")!!
        private val WORLD_CLASS: Class<*> = MinecraftReflection.getNMSClass("World")!!
        private val WORLD_SERVER_CLASS: Class<*> = MinecraftReflection.getNMSClass("WorldServer")!!

        private val PLAYER_INTERACT_MANAGER_CLASS: Class<*> = MinecraftReflection.getNMSClass("PlayerInteractManager")!!
        private val PLAYER_INTERACT_MANAGER_CONSTRUCTOR: Constructor<*> = Reflection.getDeclaredConstructor(
            PLAYER_INTERACT_MANAGER_CLASS,
            WORLD_CLASS
        )!!

        private val ENTITY_PLAYER_CLASS: Class<*> = MinecraftReflection.getNMSClass("EntityPlayer")!!
        private val ENTITY_PLAYER_CONSTRUCTOR: Constructor<*> = Reflection.getDeclaredConstructor(
            ENTITY_PLAYER_CLASS,
            MINECRAFT_SERVER_CLASS,
            WORLD_SERVER_CLASS,
            MinecraftReflection.getGameProfileClass(),
            PLAYER_INTERACT_MANAGER_CLASS
        )!!
    }

}
