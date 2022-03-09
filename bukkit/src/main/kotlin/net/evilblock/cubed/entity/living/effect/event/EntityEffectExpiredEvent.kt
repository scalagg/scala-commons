package net.evilblock.cubed.entity.living.effect.event

import net.evilblock.cubed.entity.living.effect.EntityEffect
import net.evilblock.cubed.entity.living.LivingEntity
import net.evilblock.cubed.event.PluginEvent

class EntityEffectExpiredEvent(val entity: LivingEntity, val effect: EntityEffect) : PluginEvent()
