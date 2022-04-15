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

            // TODO: add hitEntity and hitBlock into spigot fork
//            val location = (event.hitBlock?.location ?: event.hitEntity?.location) ?: return
//
//            for (entity in EntityManager.getEntities()) {
//                if (entity is LivingEntity) {
//                    if (!entity.attackable || entity.dead) {
//                        continue
//                    }
//
//                    if (entity.location.world != location.world) {
//                        continue
//                    }
//
//                    val distance = entity.location.distance(location)
//                    if (distance > 4.0) {
//                        continue
//                    }
//
//                    val attackEvent = EntityDamageEvent(event.entity, entity, 0.0)
//                    attackEvent.call()
//
//                    if (attackEvent.isCancelled) {
//                        return
//                    }
//
//                    for (effect in potion.effects) {
//                        when (effect.type) {
//                            PotionEffectType.HEAL -> {
//                                val totalHeal = if (effect.amplifier == 0) {
//                                    4.0
//                                } else {
//                                    8.0
//                                }
//
//                                val percentage = (distance / 4.0) * 100.0
//                                val heal = totalHeal * percentage
//                                val maxHeal = 20.0 - heal
//
//                                entity.health += maxHeal
//                            }
//                            PotionEffectType.HARM -> {
//                                val totalDamage = if (effect.amplifier == 0) {
//                                    4.0
//                                } else {
//                                    8.0
//                                }
//
//                                val percentage = (distance / 4.0) * 100.0
//                                val damage = totalDamage * percentage
//
//                                entity.attack(event.entity, damage, true)
//                            }
//                            PotionEffectType.POISON -> {
//                                val percentage = (distance / 4.0) * 100.0
//                                val duration = ((effect.duration / 20L) * 1000.0) * percentage
//
//                                entity.effects.add(EntityEffect(PoisonEffectType, duration.toLong()))
//                            }
//                        }
//                    }
//                }
//            }
        }
    }

}
