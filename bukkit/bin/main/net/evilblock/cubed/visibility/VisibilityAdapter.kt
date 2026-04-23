package net.evilblock.cubed.visibility

import org.bukkit.entity.Player

interface VisibilityAdapter {

    fun getAction(toRefresh: Player, refreshFor: Player): VisibilityAction

    /**
     * Returns the virtual appearance state between two players.
     */
    fun getVirtualAppearanceBetween(viewer: Player, viewed: Player): VirtualVisibility
    {
        return VirtualVisibility.NEUTRAL
    }

}
