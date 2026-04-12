

package net.evilblock.cubed.menu.protocol

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClientStatus
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCloseWindow
import io.netty.util.internal.ConcurrentSet
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.menu.protocol.event.PlayerCloseInventoryEvent
import net.evilblock.cubed.menu.protocol.event.PlayerOpenInventoryEvent
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Bukkit
import java.util.*

object InventoryPacketAdapter : PacketListenerAbstract() {
    private var registered = false

    fun configure()
    {
        if (registered) {
            return
        }

        PacketEvents.getAPI().eventManager.registerListener(this)
        registered = true
    }

    override fun onPacketReceive(event: PacketReceiveEvent) {
        val player = event.getPlayer<org.bukkit.entity.Player>() ?: return

        if (
            event.packetType === PacketType.Play.Client.CLIENT_STATUS &&
            WrapperPlayClientClientStatus(event).action == WrapperPlayClientClientStatus.Action.OPEN_INVENTORY_ACHIEVEMENT
        ) {
            if (!currentlyOpen.contains(player.uniqueId)) {
                Tasks.sync {
                    Bukkit.getPluginManager()
                        .callEvent(
                            PlayerOpenInventoryEvent(player)
                        )
                }
            }

            currentlyOpen.add(player.uniqueId)
        } else if (event.packetType === PacketType.Play.Client.CLOSE_WINDOW) {
            WrapperPlayClientCloseWindow(event)
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
    var currentlyOpen: ConcurrentSet<UUID> = ConcurrentSet()
}
