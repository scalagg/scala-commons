/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.bungee

import com.google.common.io.ByteStreams
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.ScalaCommonsSpigot
import org.bukkit.entity.Player
import java.util.logging.Level

@Service
object BungeeUtil
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    @Configure
    fun configure()
    {
        this.plugin.server
            .messenger.registerOutgoingPluginChannel(
                this.plugin, "BungeeCord"
            )
    }

    @JvmStatic
    fun sendToServer(player: Player, server: String)
    {
        kotlin.runCatching {
            val out = ByteStreams.newDataOutput()
            out.writeUTF("Connect")
            out.writeUTF(server)

            player.sendPluginMessage(
                this.plugin, "BungeeCord", out.toByteArray()
            )
        }.onFailure {
            plugin.logger.log(Level.WARNING, "Unable to send plugin message to ${player.name}", it)
        }
    }
}
