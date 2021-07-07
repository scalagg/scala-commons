package net.evilblock.cubed.entity.living.effect.event

import net.evilblock.cubed.entity.living.LivingEntity
import net.evilblock.cubed.plugin.PluginEvent

class EntityEffectHealedEvent(val entity: LivingEntity, var healed: Double) : PluginEvent()