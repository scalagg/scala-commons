/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.logging.listener

import gg.scala.commons.annotations.Listeners
import net.evilblock.cubed.Cubed
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerCommandEvent

@Listeners
object ConsoleCommandLogListeners : Listener {

    @EventHandler
    fun onServerCommandEvent(event: ServerCommandEvent) {
        if (event.sender is ConsoleCommandSender) {
            Cubed.instance.logger.info("[Console Command] ${event.command}")
        }
    }

}
