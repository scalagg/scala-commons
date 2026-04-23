package net.evilblock.cubed.util.bukkit

import com.google.common.collect.Lists
import com.google.common.collect.Sets
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import java.util.*
import java.util.function.Predicate
import kotlin.math.abs

open class BlockScanner(
    var xMinDistance: Int = 0,
    var xMaxDistance: Int = 0,
    var yMinDistance: Int = 0,
    var yMaxDistance: Int = 0,
    var zMinDistance: Int = 0,
    var zMaxDistance: Int = 0
) {

    constructor(scanner: BlockScanner) : this(
        scanner.xMinDistance,
        scanner.xMaxDistance,
        scanner.yMinDistance,
        scanner.yMaxDistance,
        scanner.zMinDistance,
        scanner.zMaxDistance
    )

    open fun after(blocks: Int): BlockScanner {
        xMinDistance = blocks
        yMinDistance = blocks
        zMinDistance = blocks
        return this
    }

    open fun within(blocks: Int): BlockScanner {
        xMaxDistance = blocks
        yMaxDistance = blocks
        zMaxDistance = blocks
        return this
    }

    open fun afterX(blocks: Int): BlockScanner {
        xMinDistance = blocks
        return this
    }

    open fun withinX(blocks: Int): BlockScanner {
        xMaxDistance = blocks
        return this
    }

    open fun afterY(blocks: Int): BlockScanner {
        yMinDistance = blocks
        return this
    }

    open fun withinY(blocks: Int): BlockScanner {
        yMaxDistance = blocks
        return this
    }

    open fun afterZ(blocks: Int): BlockScanner {
        xMinDistance = blocks
        return this
    }

    open fun withinZ(blocks: Int): BlockScanner {
        xMaxDistance = blocks
        return this
    }

    open fun forType(type: Material): BlockScannerTypesStage {
        return BlockScannerTypesStage(this, type)
    }

    open fun matches(b: Block): Boolean {
        return true
    }

    fun scan(start: Location, ignoreUnloaded: Boolean): List<Block> {
        val w = start.world
        val blocks: MutableList<Block> = Lists.newArrayList()
        val notLoaded: MutableSet<Long> = Sets.newHashSet()
        val sX = start.blockX
        val sY = start.blockY
        val sZ = start.blockZ

        for (x in sX - xMaxDistance..sX + xMaxDistance) {
            if (xMinDistance == 0 || abs(x - sX) >= xMinDistance) {
                for (z in sZ - zMaxDistance..sZ + zMaxDistance) {
                    if (zMinDistance == 0 || abs(z - sZ) >= zMinDistance) {
                        if (ignoreUnloaded) {
                            val k = toKey(x shr 4, z shr 4)
                            if (notLoaded.contains(k)) {
                                continue
                            }
                            if (!start.world.isChunkLoaded(x shr 4, z shr 4)) {
                                notLoaded.add(k)
                                continue
                            }
                        }

                        for (y in sY - yMaxDistance..sY + yMaxDistance) {
                            if (yMinDistance == 0 || abs(y - sY) >= yMinDistance) {
                                val b = w.getBlockAt(x, y, z)
                                if (matches(b)) {
                                    blocks.add(b)
                                }
                            }
                        }
                    }
                }
            }
        }
        return blocks
    }

    fun toKey(x: Int, z: Int): Long {
        return x.toLong() and 0xFFFFFFFFL or (z.toLong() and 0xFFFFFFFFL) shl 32
    }

    open class BlockScannerTypesStage : BlockScanner {
        val types: MutableSet<Material>

        internal constructor(scanner: BlockScannerTypesStage) : super(scanner) {
            types = EnumSet.copyOf(scanner.types)
        }

        internal constructor(scanner: BlockScanner, type: Material) : super(scanner) {
            types = EnumSet.of(type)
        }

        internal constructor(scanner: BlockScanner, types: Set<Material>) : super(scanner) {
            this.types = EnumSet.copyOf(types)
        }

        override fun forType(type: Material): BlockScannerTypesStage {
            types.add(type)
            return this
        }

        open fun forMany(vararg type: Material): BlockScannerTypesStage {
            types.addAll(listOf(*type))
            return this
        }

        override fun matches(b: Block): Boolean {
            return super.matches(b) && types.contains(b.type)
        }

        override fun after(blocks: Int): BlockScannerTypesStage {
            xMinDistance = blocks
            yMinDistance = blocks
            zMinDistance = blocks
            return this
        }

        override fun within(blocks: Int): BlockScannerTypesStage {
            xMaxDistance = blocks
            yMaxDistance = blocks
            zMaxDistance = blocks
            return this
        }

        override fun afterX(blocks: Int): BlockScannerTypesStage {
            xMinDistance = blocks
            return this
        }

        override fun withinX(blocks: Int): BlockScannerTypesStage {
            xMaxDistance = blocks
            return this
        }

        override fun afterY(blocks: Int): BlockScannerTypesStage {
            yMinDistance = blocks
            return this
        }

        override fun withinY(blocks: Int): BlockScannerTypesStage {
            yMaxDistance = blocks
            return this
        }

        override fun afterZ(blocks: Int): BlockScannerTypesStage {
            xMinDistance = blocks
            return this
        }

        override fun withinZ(blocks: Int): BlockScannerTypesStage {
            xMaxDistance = blocks
            return this
        }

        fun where(predicate: Predicate<Block>): BlockScannerTypesWhereStage {
            return BlockScannerTypesWhereStage(this, predicate)
        }

//        override fun clone(): BlockScannerTypesStage {
//            return BlockScannerTypesStage(this, types)
//        }
    }

    class BlockScannerTypesWhereStage(scanner: BlockScannerTypesStage, var test: Predicate<Block>) : BlockScannerTypesStage(scanner) {
        override fun forType(type: Material): BlockScannerTypesWhereStage {
            types.add(type)
            return this
        }

        override fun forMany(vararg type: Material): BlockScannerTypesWhereStage {
            types.addAll(listOf(*type))
            return this
        }

        override fun matches(b: Block): Boolean {
            return super.matches(b) && test.test(b)
        }

        override fun after(blocks: Int): BlockScannerTypesWhereStage {
            xMinDistance = blocks
            yMinDistance = blocks
            zMinDistance = blocks
            return this
        }

        override fun within(blocks: Int): BlockScannerTypesWhereStage {
            xMaxDistance = blocks
            yMaxDistance = blocks
            zMaxDistance = blocks
            return this
        }

        override fun afterX(blocks: Int): BlockScannerTypesWhereStage {
            xMinDistance = blocks
            return this
        }

        override fun withinX(blocks: Int): BlockScannerTypesWhereStage {
            xMaxDistance = blocks
            return this
        }

        override fun afterY(blocks: Int): BlockScannerTypesWhereStage {
            yMinDistance = blocks
            return this
        }

        override fun withinY(blocks: Int): BlockScannerTypesWhereStage {
            yMaxDistance = blocks
            return this
        }

        override fun afterZ(blocks: Int): BlockScannerTypesWhereStage {
            xMinDistance = blocks
            return this
        }

        override fun withinZ(blocks: Int): BlockScannerTypesWhereStage {
            xMaxDistance = blocks
            return this
        }

        fun and(predicate: Predicate<Block>): BlockScannerTypesWhereStage {
            test = test.and(predicate)
            return this
        }

        fun or(predicate: Predicate<Block>): BlockScannerTypesWhereStage {
            test = test.or(predicate)
            return this
        }

//        override fun clone(): BlockScannerTypesWhereStage {
//            return BlockScannerTypesWhereStage(this, test)
//        }
    }
}