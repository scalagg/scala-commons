package gg.scala.commons.modern

import org.bukkit.Bukkit

/**
 * Access to modern Minecraft server APIs.
 * Provides compatibility for 1.9+ server features.
 *
 * @author Limo
 * @date 2/5/2026
 */
object ModernAccess
{
    /**
     * Get the current server tick.
     * Uses reflection to access the appropriate method for the server version.
     */
    fun currentTick(): Int
    {
        return try {
            val serverClass = Class.forName("org.bukkit.Bukkit")
            val method = serverClass.getMethod("getCurrentTick")
            method.invoke(null) as Int
        } catch (e: Exception) {
            try {
                val server = Bukkit.getServer()
                val craftServerClass = server.javaClass
                val getServerMethod = craftServerClass.getMethod("getServer")
                val minecraftServer = getServerMethod.invoke(server)
                val ticksField = minecraftServer.javaClass.getField("currentTick")
                ticksField.getInt(minecraftServer)
            } catch (ex: Exception) {
                (System.currentTimeMillis() / 50).toInt()
            }
        }
    }
}
