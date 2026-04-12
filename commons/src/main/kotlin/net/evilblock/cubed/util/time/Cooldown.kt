

package net.evilblock.cubed.util.time

class Cooldown<T>(private val cooldownTime: Long) {

    private val map: MutableMap<T, Long> = hashMapOf()

    fun isOnCooldown(key: T): Boolean {
        return map.containsKey(key) && System.currentTimeMillis() < map[key]!!
    }

    fun putOnCooldown(key: T) {
        map[key] = System.currentTimeMillis() + cooldownTime
    }

    fun removeCooldown(key: T) {
        map.remove(key)
    }

}
