package net.evilblock.cubed.nametag.update

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * @author GrowlyX
 * @since 9/25/2021
 */
class NametagUpdateEvent(
    val player: Player
) : Event() {

    companion object {
        @JvmStatic
        val HANDLERS = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
}
