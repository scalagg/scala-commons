package net.evilblock.cubed.entity.hooks

import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 2/20/2023
 */
object EntityHooks
{
    var entityVisibilityDistance = { _: Player -> 32.0 }

    fun entityVisibilityDistance(lambda: (Player) -> Double)
    {
        this.entityVisibilityDistance = lambda
    }
}
