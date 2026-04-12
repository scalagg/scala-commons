package gg.scala.commons.spatial

import org.bukkit.Location
import org.bukkit.util.Vector

/**
 * @author Limo
 * @date 2/5/2026
 */

operator fun Location.minus(other: Location): Vector
{
    return Vector(
        this.x - other.x,
        this.y - other.y,
        this.z - other.z
    )
}

operator fun Location.plus(other: Location): Location
{
    return this.clone().add(other.x, other.y, other.z)
}

operator fun Location.plus(vector: Vector): Location
{
    return this.clone().add(vector)
}

fun Location.vectorSubtract(other: Location): Vector
{
    return Vector(
        this.x - other.x,
        this.y - other.y,
        this.z - other.z
    )
}
