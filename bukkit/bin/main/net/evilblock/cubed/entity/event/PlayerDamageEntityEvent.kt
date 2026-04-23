/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.event

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.npc.action.DamageData
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerDamageEntityEvent(val player: Player, val entity: Entity, val damageData: DamageData = DamageData()) : Event(), Cancellable {

    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        this.cancelled = cancel
    }

    companion object {
        @JvmStatic val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

}