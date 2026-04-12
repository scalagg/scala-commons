
package net.evilblock.cubed.util.bukkit

import org.bukkit.Location

enum class Direction {

    NORTH, EAST, SOUTH, WEST;

    fun move(location: Location, blocks: Double) {
        when (this) {
            NORTH -> location.z = location.z - blocks
            EAST -> location.x = location.x + blocks
            SOUTH -> location.z = location.z + blocks
            WEST -> location.x = location.x - blocks
        }
    }

    fun getOpposite(): Direction {
        return when (this) {
            NORTH -> SOUTH
            EAST -> WEST
            SOUTH -> NORTH
            WEST -> EAST
        }
    }

    companion object {
        @JvmStatic
        fun getFromYaw(yaw: Float): Direction {
            var rotation = ((yaw - 90.0F) % 360.0F).toDouble()

            if (rotation < 0.0) {
                rotation += 360.0
            }

            return if (0.0 <= rotation && rotation < 45.0) {
                WEST
            } else if (45.0 <= rotation && rotation < 135.0) {
                NORTH
            } else if (135.0 <= rotation && rotation < 225.0) {
                EAST
            } else if (225.0 <= rotation && rotation < 315.0) {
                SOUTH
            } else {
                WEST
            }
        }
    }

}