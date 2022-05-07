/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.logging.listener

import gg.scala.commons.annotations.Listeners
import gg.scala.flavor.inject.Inject
import net.evilblock.cubed.ScalaCommonsSpigot
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerCommandEvent

@Listeners
object ConsoleCommandLogListeners : Listener
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    @EventHandler
    fun onServerCommandEvent(event: ServerCommandEvent)
    {
        if (event.sender is ConsoleCommandSender)
        {
            plugin.logger.info("[Console Command] ${event.command}")
        }
    }
}
