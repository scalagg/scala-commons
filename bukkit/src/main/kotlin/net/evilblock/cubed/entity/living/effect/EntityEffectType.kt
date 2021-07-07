package net.evilblock.cubed.entity.living.effect

import org.bukkit.Color

abstract class EntityEffectType(val id: String, val name: String) {

    abstract fun tick(effect: EntityEffect)

    open fun isAmbient(): Boolean {
        return false
    }

    open fun isShowParticles(): Boolean {
        return false
    }

    open fun getParticlesColor(): Color? {
        throw IllegalStateException("Particle effect does not have a color")
    }

    open fun getParticlesAmplifier(): Int {
        return 0
    }

}