package net.evilblock.cubed.entity.living.effect.event

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.living.effect.EntityEffect
import net.evilblock.cubed.plugin.PluginEvent

class EntityEffectRemovedEvent(val entity: Entity, val effect: EntityEffect) : PluginEvent()