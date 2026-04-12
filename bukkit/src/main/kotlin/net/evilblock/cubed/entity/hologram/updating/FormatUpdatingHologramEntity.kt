

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