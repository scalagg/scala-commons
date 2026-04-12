package net.evilblock.cubed.visibility

import org.bukkit.entity.Player

/**
 * @author Limo
 * @date 2/10/2026
 */
interface PlayerListVisibilityPolicy
{
    fun shouldShow(viewer: Player, target: Player): Boolean

    companion object
    {
        val Global: PlayerListVisibilityPolicy = object : PlayerListVisibilityPolicy {
            override fun shouldShow(viewer: Player, target: Player) = true
        }

        val World: PlayerListVisibilityPolicy = object : PlayerListVisibilityPolicy {
            override fun shouldShow(viewer: Player, target: Player): Boolean {
                return viewer.world == target.world
            }
        }
    }
}
