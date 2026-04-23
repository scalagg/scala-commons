/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.bukkit.generator

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.generator.ChunkGenerator
import java.util.*

class EmptyChunkGenerator : ChunkGenerator() {

    override fun generateBlockSections(
        world: World?,
        random: Random?,
        x: Int,
        z: Int,
        biomes: BiomeGrid?
    ): Array<ByteArray?> {
        return arrayOfNulls(world!!.maxHeight / 16)
    }

    override fun getFixedSpawnLocation(world: World?, random: Random?): Location {
        return Location(world, 0.0, 68.0, 0.0)
    }

}