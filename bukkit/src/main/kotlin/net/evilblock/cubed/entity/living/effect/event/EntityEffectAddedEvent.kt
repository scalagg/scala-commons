package net.evilblock.cubed.entity.living.effect.event

import net.evilblock.cubed.entity.living.effect.EntityEffect
import net.evilblock.cubed.entity.living.LivingEntity
import net.evilblock.cubed.plugin.PluginEvent

class EntityEffectAddedEvent(val entity: LivingEntity, val effect: EntityEffect) : PluginEvent()