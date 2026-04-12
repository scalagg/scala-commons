package gg.scala.commons.spatial

import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.util.Vector

fun Location.toPosition() = Position(
    x = x,
    y = y,
    z = z,
    yaw = yaw,
    pitch = pitch
)

fun Vector.toPosition() = Position(
    x = x,
    y = y,
    z = z,
    yaw = 0f,
    pitch = 0f
)

fun Orientation.toBlockFace() = BlockFace.valueOf(name)
