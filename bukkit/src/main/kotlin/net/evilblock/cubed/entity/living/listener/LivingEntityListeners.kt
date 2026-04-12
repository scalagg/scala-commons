package net.evilblock.cubed.entity.living.listener

import gg.scala.commons.annotations.Listeners
import org.bukkit.entity.Player
import org.bukkit.entity.ThrownPotion
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent

@Listeners
object LivingEntityListeners : Listener {

    @EventHandler
    fun onProjectileHitEvent(event: ProjectileHitEvent) {
        if (event.entity is ThrownPotion) {
            if (event.entity.shooter !is Player) {
                return
            }

            val potion = event.entity as ThrownPotion
            if (potion.effects.isEmpty()) {
                return
            }

        }
    }

}
