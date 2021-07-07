package net.evilblock.cubed.util.nms.particle

import net.evilblock.cubed.util.bukkit.ColorUtil
import org.bukkit.Color
import org.bukkit.Location

data class ParticleMeta(val location: Location, val particle: Any) {

    constructor(location: Location,
                particle: Any,
                deltaX: Float,
                deltaY: Float,
                deltaZ: Float,
                speed: Float,
                amount: Int) : this(location, particle) {
        this.offsetX = deltaX
        this.offsetY = deltaY
        this.offsetZ = deltaZ
        this.speed = speed
        this.amount = amount
    }

    constructor(location: Location,
                particle: Any,
                color: Color,
                speed: Float,
                amount: Int) : this(location, particle) {
        this.offsetX = ColorUtil.convertColorValue(color.red.toDouble()).toFloat()
        this.offsetY = ColorUtil.convertColorValue(color.green.toDouble()).toFloat()
        this.offsetZ = ColorUtil.convertColorValue(color.blue.toDouble()).toFloat()
        this.speed = speed
        this.amount = amount
    }

    var offsetX = 0.0F
    var offsetY = 0.0F
    var offsetZ = 0.0F
    var speed = 1.0F
    var amount = 1

}