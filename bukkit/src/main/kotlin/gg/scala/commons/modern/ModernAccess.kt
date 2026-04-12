package gg.scala.commons.modern

import org.bukkit.Bukkit

/**
 * @author Limo
 * @date 2/5/2026
 */
object ModernAccess
{

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
