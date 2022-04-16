package net.evilblock.cubed.entity.npc.util

import com.cryptomorin.xseries.XMaterial
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace

object NpcUtils
{
    /**
     * A map of block types to their heights, used
     * to handle edge cases when updating an NPC's location.
     */
    val BLOCK_HEIGHTS = mutableMapOf(
        "SLAB" to 0.3,
        "STAIRS" to 0.5,
        "TRAPDOOR" to 0.2,
        "CARPET" to 0.0625
    )

    @JvmStatic
    fun findFloor(block: Block, checks: Int = 10): Location?
    {
        if (isEmpty(block, 2))
        {
            return block.location
        }

        for (i in 0 until checks)
        {
            var check = block.getRelative(0, i + 1, 0)
            if (check.getRelative(BlockFace.DOWN).type == Material.AIR && isEmpty(check, 2))
            {
                return check.location
            }

            check = block.getRelative(0, -1 - i, 0)
            if (check.getRelative(BlockFace.DOWN).type == Material.AIR && isEmpty(check, 2))
            {
                return check.location
            }
        }

        return block.location
    }

    @JvmStatic
    fun isEmpty(block: Block, height: Int): Boolean
    {
        if (height == 0)
        {
            return true
        }

        return if (block.type == Material.AIR)
        {
            return isEmpty(block.getRelative(BlockFace.UP), height - 1)
        } else
        {
            false
        }
    }

}
