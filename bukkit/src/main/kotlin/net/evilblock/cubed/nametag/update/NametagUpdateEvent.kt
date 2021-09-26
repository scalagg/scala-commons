package net.evilblock.cubed.nametag.update

import net.evilblock.cubed.plugin.PluginEvent
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/25/2021
 */
class NametagUpdateEvent(
    val player: Player
) : PluginEvent()
