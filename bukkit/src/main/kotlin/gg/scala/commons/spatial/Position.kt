package gg.scala.commons.spatial

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector
import kotlin.math.sqrt

data class Position(
    val x: Double, val y: Double, val z: Double,
    val yaw: Float = 0.0f, val pitch: Float = 0.0f
)
{
    constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble())

    override fun toString() = "($x, $y, $z)"

    fun isEqualToVector(other: Position) = other.x == x && other.y == y && other.z == z

    fun toVector() = Vector(x, y, z)
    fun toLocation(world: World) = Location(world, x, y, z, yaw, pitch)

    operator fun plus(other: Position): Position = Position(
        x + other.x, y + other.y, z + other.z, yaw, pitch
    )

    operator fun minus(other: Position): Position = Position(
        x - other.x, y - other.y, z - other.z, yaw, pitch
    )

    fun vectorSubtract(other: Position): Position = Position(
        x - other.x, y - other.y, z - other.z, yaw, pitch
    )

    fun distance(other: Position): Double
    {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return sqrt(dx * dx + dy * dy + dz * dz)
    }

    fun distanceSquared(other: Position): Double
    {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return dx * dx + dy * dy + dz * dz
    }
}
