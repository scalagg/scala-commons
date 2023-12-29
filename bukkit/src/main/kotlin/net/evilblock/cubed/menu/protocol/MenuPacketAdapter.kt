/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.protocol

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.menu.Menu

object MenuPacketAdapter : PacketAdapter(
    AdapterParameteters()
        .plugin(ScalaCommonsSpigot.instance)
        .types(PacketType.Play.Client.CLOSE_WINDOW)
        .optionAsync()
)
{
    override fun onPacketReceiving(event: PacketEvent)
    {
        val openedMenus = Menu.currentlyOpenedMenus

        if (openedMenus.containsKey(event.player.uniqueId))
            openedMenus[event.player.uniqueId]?.manualClose = true
    }
}
