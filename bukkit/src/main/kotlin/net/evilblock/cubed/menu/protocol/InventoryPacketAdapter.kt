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
import com.comphenix.protocol.wrappers.EnumWrappers
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.menu.protocol.event.PlayerCloseInventoryEvent
import net.evilblock.cubed.menu.protocol.event.PlayerOpenInventoryEvent
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Bukkit
import java.util.*

object InventoryPacketAdapter : PacketAdapter(ScalaCommonsSpigot.instance, PacketType.Play.Client.CLIENT_COMMAND, PacketType.Play.Client.CLOSE_WINDOW, PacketType.Play.Server.MOUNT) {

    override fun onPacketReceiving(event: PacketEvent?) {
        val player = event!!.player
        val packet = event.packet
//
//        println(packet.type.name())
//
//        if (packet.type == PacketType.Play.Server.MOUNT)
//        {
//            if (
//                ScalaCommonsSpigot.instance
//                    .config<CommonsGeneralConfig>()
//                    .doNotAllowMountOnCommonsEntity
//            )
//            {
//                val wrapped = WrapperPlayServerMount(event.packet)
//                val entityIds = EntityHandler
//                    .getEntities().map(Entity::id)
//
//                println("[Debug] $entityIds || ${wrapped.entityID}")
//
//                if (entityIds.contains(wrapped.entityID))
//                {
//                    event.isCancelled = true
//                }
//            }
//
//            return
//        }

        if (packet.type === PacketType.Play.Client.CLIENT_COMMAND && packet.clientCommands.size() != 0 && packet.clientCommands.read(0) == EnumWrappers.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
            if (!currentlyOpen.contains(player.uniqueId)) {
                Tasks.sync {
                    Bukkit.getPluginManager()
                        .callEvent(
                            PlayerOpenInventoryEvent(player)
                        )
                }
            }

            currentlyOpen.add(player.uniqueId)
        } else if (packet.type === PacketType.Play.Client.CLOSE_WINDOW) {
            if (currentlyOpen.contains(player.uniqueId)) {
                Tasks.sync {
                    Bukkit.getPluginManager()
                        .callEvent(
                            PlayerCloseInventoryEvent(player)
                        )
                }
            }

            currentlyOpen.remove(player.uniqueId)
        }
    }

    @JvmStatic
    var currentlyOpen: HashSet<UUID> = HashSet()

}
