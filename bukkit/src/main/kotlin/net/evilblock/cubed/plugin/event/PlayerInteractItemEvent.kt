package net.evilblock.cubed.plugin.event

import net.evilblock.cubed.plugin.PluginEvent
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack

/**
 * @author puugz
 * @since 27/08/2021 16:46
 */
class PlayerInteractItemEvent(
    val player: Player,
    val item: ItemStack,
    val action: Action
): PluginEvent(), Cancellable {

    var useItem: Event.Result = Event.Result.DEFAULT

    override fun isCancelled(): Boolean {
        return useItem == Event.Result.DENY
    }

    override fun setCancelled(p0: Boolean) {
        useItem = if (p0) {
            Event.Result.DENY
        } else {
            Event.Result.ALLOW
        }
    }
}

enum class Action {
    LEFT_CLICK_BLOCK,
    RIGHT_CLICK_BLOCK,
    LEFT_CLICK_AIR,
    RIGHT_CLICK_AIR;

    val isRightClick: Boolean
        get() = this == RIGHT_CLICK_AIR || this == RIGHT_CLICK_BLOCK

    val isLeftClick: Boolean
        get() = this == LEFT_CLICK_AIR || this == LEFT_CLICK_BLOCK
}