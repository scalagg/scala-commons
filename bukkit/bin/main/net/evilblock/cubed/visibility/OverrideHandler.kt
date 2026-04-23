package net.evilblock.cubed.visibility

import org.bukkit.entity.Player

interface OverrideHandler {

    fun getAction(toRefresh: Player, refreshFor: Player): OverrideAction

}