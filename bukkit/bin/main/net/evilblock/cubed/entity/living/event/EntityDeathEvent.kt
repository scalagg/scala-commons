package net.evilblock.cubed.entity.living.event

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.event.PluginEvent

class EntityDeathEvent(val killer: org.bukkit.entity.Entity, val entity: Entity) : PluginEvent()
