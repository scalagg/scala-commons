package net.evilblock.cubed.nametag.update

import net.evilblock.cubed.event.PluginEvent
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

class NametagUpdateEvent(
    val player: Player
) : PluginEvent() {

    companion object {
        @JvmStatic
        val HANDLERS = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
}
