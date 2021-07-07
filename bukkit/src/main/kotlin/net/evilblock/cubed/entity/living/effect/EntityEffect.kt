package net.evilblock.cubed.entity.living.effect

class EntityEffect(
    val effectType: EntityEffectType,
    val duration: Long
) {

    val addedAt: Long = System.currentTimeMillis()

    fun getRemainingDuration(): Long {
        return (addedAt + duration) - System.currentTimeMillis()
    }

    fun getRemainingSeconds(): Int {
        return (getRemainingDuration() / 1000.0).toInt()
    }

    fun isPermanent(): Boolean {
        return duration == Long.MAX_VALUE
    }

    fun isExpired(): Boolean {
        return System.currentTimeMillis() >= (addedAt + duration)
    }

}