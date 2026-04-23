/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.protocol

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.menu.Menu

object MenuPacketAdapter : PacketListenerAbstract()
{
    private var registered = false

    fun configure()
    {
        if (registered) {
            return
        }

        PacketEvents.getAPI().eventManager.registerListener(this)
        registered = true
    }

    override fun onPacketReceive(event: PacketReceiveEvent)
    {
        if (event.packetType != PacketType.Play.Client.CLOSE_WINDOW) {
            return
        }

        val player = event.getPlayer<org.bukkit.entity.Player>() ?: return
        Menu.currentlyOpenedMenus[player.uniqueId]?.manualClose = true
    }
}
