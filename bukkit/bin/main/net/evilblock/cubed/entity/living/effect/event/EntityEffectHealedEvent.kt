package net.evilblock.cubed.entity.living.effect.event

import net.evilblock.cubed.entity.living.LivingEntity
import net.evilblock.cubed.event.PluginEvent

class EntityEffectHealedEvent(val entity: LivingEntity, var healed: Double) : PluginEvent()
