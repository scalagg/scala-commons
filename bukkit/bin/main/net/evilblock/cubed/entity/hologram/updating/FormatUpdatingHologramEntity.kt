/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.hologram.updating

import org.bukkit.Location

open class FormatUpdatingHologramEntity(text: String, location: Location) : UpdatingHologramEntity(text, location) {

    var format: MutableList<String> = arrayListOf(text)

    override fun getNewLines(): List<String> {
        return format
    }

    override fun getTickInterval(): Long {
        return 500L
    }

}