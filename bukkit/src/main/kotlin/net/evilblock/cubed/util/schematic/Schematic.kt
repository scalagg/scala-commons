/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.schematic

import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools
import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.minecraft.server.v1_8_R3.NBTTagList
import org.bukkit.util.Vector
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.experimental.and

class Schematic(
    val name: String,
    val file: File,
    val width: Short,
    val length: Short,
    val height: Short,
    val blocks: ShortArray,
    val data: ByteArray,
    val tileEntities: NBTTagList
) {

    fun getBlockMap(): Array<Array<Array<SchematicBlock>>> {
        val blockMap: Array<Array<Array<SchematicBlock?>>> = Array(width.toInt()) { Array(height.toInt()) { arrayOfNulls<SchematicBlock>(length.toInt()) } }
        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until length) {
                    val index = y * width * length + z * width + x
                    blockMap[x][y][z] = SchematicBlock(blocks[index].toInt(), data[index], Vector(x, y, z))
                }
            }
        }
        return blockMap as Array<Array<Array<SchematicBlock>>>
    }

    fun findTileEntity(x: Int, y: Int, z: Int): NBTTagCompound? {
        for (i in 0 until tileEntities.size()) {
            val compound = tileEntities.get(i)
            if (x == compound.getInt("x") && y == compound.getInt("y") && z == compound.getInt("z")) {
                return compound
            }
        }
        return null
    }

    companion object {
        @Throws(FileNotFoundException::class, IOException::class)
        @JvmStatic
        fun loadSchematic(file: File): Schematic {
            val name = file.name.replace(".schematic", "")
            if (!file.exists()) {
                throw FileNotFoundException("$name.schematic file not found")
            }

            FileInputStream(file).use { stream ->
                val nbtData = NBTCompressedStreamTools.a(stream)
                val width = nbtData.getShort("Width")
                val height = nbtData.getShort("Height")
                val length = nbtData.getShort("Length")
                val rawBlocks = nbtData.getByteArray("Blocks")
                val rawData = nbtData.getByteArray("Data")
                var addId = ByteArray(0)
                val tileEntities = nbtData.getList("TileEntities", 10) ?: NBTTagList()

                if (nbtData.hasKey("AddBlocks")) {
                    addId = nbtData.getByteArray("AddBlocks")
                }

                val blocks = ShortArray(rawBlocks.size)
                for (index in rawBlocks.indices) {
                    if (index shr 1 >= addId.size) {
                        blocks[index] = (rawBlocks[index].toInt() and 0xFF).toShort()
                    } else {
                        if (index and 1 == 0) {
                            blocks[index] = (((addId[index shr 1] and 0x0F.toByte()).toInt() shl 8) + (rawBlocks[index].toInt() and 0xFF)).toShort()
                        } else {
                            blocks[index] = (((addId[index shr 1] and 0xF0.toByte()).toInt() shl 4) + (rawBlocks[index].toInt() and 0xFF)).toShort()
                        }
                    }
                }

                return Schematic(name, file, width, length, height, blocks, rawData, tileEntities)
            }
        }
    }

}