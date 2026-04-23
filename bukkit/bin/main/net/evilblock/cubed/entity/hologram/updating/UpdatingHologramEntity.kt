/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.hologram.updating

import net.evilblock.cubed.entity.hologram.HologramEntity
import org.bukkit.Location

abstract class UpdatingHologramEntity(text: String, location: Location) : HologramEntity(text = text, location = location) {

    var lastUpdate: Long = System.currentTimeMillis()

    abstract fun getNewLines(): List<String>

    abstract fun getTickInterval(): Long

    fun canUpdate(): Boolean {
        return System.currentTimeMillis() - lastUpdate >= getTickInterval()
    }

    fun forceUpdate() {
        updateLines(getNewLines())
    }

}