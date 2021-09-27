package net.evilblock.cubed.nametag.update

import net.evilblock.cubed.plugin.PluginEvent
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

/**
 * @author GrowlyX
 * @since 9/25/2021
 */
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
