

package net.evilblock.cubed.util.bukkit

import org.bukkit.block.BlockFace
import java.util.*
import kotlin.math.roundToInt

object AngleUtils {

    private val RADIAL = arrayOf(BlockFace.WEST, BlockFace.NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST)
    private val NOTCHES: MutableMap<BlockFace, Int> = EnumMap(BlockFace::class.java)

    init {
        for (i in RADIAL.indices) {
            NOTCHES[RADIAL[i]] = i
        }
    }

    @JvmStatic
    fun yawToFace(yaw: Float): BlockFace {
        return RADIAL[(yaw / 45.0F).roundToInt() and 0x7]
    }

    @JvmStatic
    fun faceToYaw(face: BlockFace): Int {
        return wrapAngle(45 * NOTCHES.getOrDefault(face, 0))
    }

    @JvmStatic
    fun wrapAngle(angle: Float): Int {
        return wrapAngle(angle.toInt())
    }

    @JvmStatic
    fun wrapAngle(angle: Int): Int {
        var angle = angle

        angle %= 360;

        angle = (angle + 360) % 360;

        while (angle > 180) {
            angle -= 360
        }

        return angle
    }

    @JvmStatic
    fun yawToBytes(yaw: Float): Byte {
        return (yaw.toDouble() * 256.0F / 360.0F).toInt().toByte()
    }

    @JvmStatic
    fun getBlockFace(rotation: Byte): BlockFace {
        return when (rotation.toInt()) {
            0 -> BlockFace.NORTH
            1 -> BlockFace.NORTH_NORTH_EAST
            2 -> BlockFace.NORTH_EAST
            3 -> BlockFace.EAST_NORTH_EAST
            4 -> BlockFace.EAST
            5 -> BlockFace.EAST_SOUTH_EAST
            6 -> BlockFace.SOUTH_EAST
            7 -> BlockFace.SOUTH_SOUTH_EAST
            8 -> BlockFace.SOUTH
            9 -> BlockFace.SOUTH_SOUTH_WEST
            10 -> BlockFace.SOUTH_WEST
            11 -> BlockFace.WEST_SOUTH_WEST
            12 -> BlockFace.WEST
            13 -> BlockFace.WEST_NORTH_WEST
            14 -> BlockFace.NORTH_WEST
            15 -> BlockFace.NORTH_NORTH_WEST
            else -> throw AssertionError(rotation)
        }
    }

}
