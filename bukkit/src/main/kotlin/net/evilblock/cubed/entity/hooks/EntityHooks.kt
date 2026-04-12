package net.evilblock.cubed.entity.hooks

import org.bukkit.entity.Player

object EntityHooks
{
    var entityVisibilityDistance = { _: Player -> 48.0 }
    fun entityVisibilityDistance(lambda: (Player) -> Double)
    {
        this.entityVisibilityDistance = lambda
    }
}
