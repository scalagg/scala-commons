package net.evilblock.cubed.visibility

import org.bukkit.entity.Player

interface VisibilityAdapter {

    fun getAction(toRefresh: Player, refreshFor: Player): VisibilityAction

}
