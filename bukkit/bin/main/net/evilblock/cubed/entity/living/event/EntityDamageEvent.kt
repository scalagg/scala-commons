package net.evilblock.cubed.entity.living.event

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.event.PluginEvent
import org.bukkit.event.Cancellable

class EntityDamageEvent(val attacker: org.bukkit.entity.Entity?, val entity: Entity, var damage: Double) : PluginEvent(), Cancellable {

    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

}
