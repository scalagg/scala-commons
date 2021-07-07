/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.bungee

import com.google.common.io.ByteStreams
import net.evilblock.cubed.Cubed
import org.bukkit.entity.Player

object BungeeUtil {

    private var registered: Boolean = false

    private fun registerChannel() {
        Cubed.instance.server.messenger.registerOutgoingPluginChannel(Cubed.instance, "BungeeCord")
    }

    @JvmStatic
    fun sendToServer(player: Player, server: String) {
        if (!registered) {
            registerChannel()
            registered = true
        }

        try {
            val out = ByteStreams.newDataOutput()
            out.writeUTF("Connect")
            out.writeUTF(server)

            player.sendPluginMessage(Cubed.instance, "BungeeCord", out.toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}