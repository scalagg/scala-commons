package net.evilblock.cubed.util.bukkit

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.player.PlayerMoveEvent

object EventUtils {
    
    @JvmStatic
    fun hasPlayerMoved(event: PlayerMoveEvent): Boolean {
        return event.to.x != event.from.x || event.to.y != event.from.y || event.to.z != event.from.z
    }

    @JvmStatic
    fun getAttacker(damager: Entity): Player? {
        return when (damager) {
            is Player -> {
                damager
            }
            is Projectile -> {
                if (damager.shooter is Player) {
                    damager.shooter as Player
                } else {
                    null
                }
            }
            else -> {
                null
            }
        }
    }

    @JvmStatic
    fun getAttackingEntity(damager: Entity): LivingEntity? {
        return when (damager) {
            is LivingEntity -> {
                damager
            }
            is Projectile -> {
                if (damager.shooter is LivingEntity) {
                    damager.shooter as LivingEntity
                } else {
                    null
                }
            }
            else -> {
                null
            }
        }
    }

}