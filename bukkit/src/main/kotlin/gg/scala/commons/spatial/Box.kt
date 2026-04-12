package gg.scala.commons.spatial

/**
 * @author Limo
 * @date 2/5/2026
 */
data class Box(
    val xSize: Double,
    val ySize: Double,
    val zSize: Double,
    val center: Position
)
{
    val minX: Double get() = center.x - (xSize / 2)
    val maxX: Double get() = center.x + (xSize / 2)
    val minY: Double get() = center.y - (ySize / 2)
    val maxY: Double get() = center.y + (ySize / 2)
    val minZ: Double get() = center.z - (zSize / 2)
    val maxZ: Double get() = center.z + (zSize / 2)

    fun contains(x: Double, y: Double, z: Double): Boolean
    {
        return x in minX..maxX && y in minY..maxY && z in minZ..maxZ
    }

    fun contains(position: Position): Boolean
    {
        return contains(position.x, position.y, position.z)
    }

    fun toBounds(): Bounds
    {
        return Bounds(
            lowerLeft = Position(minX, minY, minZ, 0f, 0f),
            upperRight = Position(maxX, maxY, maxZ, 0f, 0f)
        )
    }
}
