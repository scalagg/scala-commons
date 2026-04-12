

package net.evilblock.cubed.util.hook

import com.sk89q.worldedit.*
import com.sk89q.worldedit.blocks.BaseBlock
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.regions.Region
import com.sk89q.worldedit.schematic.SchematicFormat
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import java.io.File

object WorldEditUtils {

    @JvmStatic
    fun getSchematicsDirectory(): File {
        return File(Bukkit.getPluginManager().getPlugin("WorldEdit").dataFolder, "schematics")
    }

    @JvmStatic
    fun getSelection(player: Player): Region? {
        return try {
            WorldEdit.getInstance().getSession(player.name).getSelection(BukkitWorld(player.world))
        } catch (e: IncompleteRegionException) {
            null
        }
    }

    @JvmStatic
    fun toCuboid(selection: Region): Cuboid {
        val world = Bukkit.getWorld(selection.world!!.name)
        val min = Location(world, selection.minimumPoint.x, selection.minimumPoint.y, selection.minimumPoint.z)
        val max = Location(world, selection.maximumPoint.x, selection.maximumPoint.y, selection.maximumPoint.z)
        return Cuboid(min, max)
    }

    @JvmStatic
    fun newEditSession(world: World): EditSession {
        val worldEditWorld = BukkitWorld(world)
        return WorldEdit.getInstance().editSessionFactory.getEditSession(worldEditWorld, Int.MAX_VALUE)
    }

    @JvmStatic
    @Throws(Exception::class)
    fun clipboard(schematicFile: File): CuboidClipboard {
        val clipboard = SchematicFormat.MCEDIT.load(schematicFile)

        

        
        clipboard.offset = Vector(0, 0, 0)
        return clipboard
    }

    @JvmStatic
    @Throws(Exception::class)
    fun paste(schematicFile: File, world: World, pasteAt: Vector, noAir: Boolean = false): CuboidClipboard {
        val clipboard = clipboard(schematicFile)
        clipboard.paste(newEditSession(world), Vector(pasteAt.x, pasteAt.y, pasteAt.z), noAir)
        return clipboard
    }

    @JvmStatic
    @Throws(Exception::class)
    fun save(schematicFile: File, saveFrom: Location) {
        val schematicSize = readSchematicSize(schematicFile)

        val newSchematic = CuboidClipboard(schematicSize, Vector(saveFrom.x, saveFrom.y, saveFrom.z))
        newSchematic.copy(newEditSession(saveFrom.world))

        SchematicFormat.MCEDIT.save(newSchematic, schematicFile)
    }

    @JvmStatic
    @Throws(Exception::class)
    fun readSchematicSize(schematicFile: File): Vector {
        return clipboard(schematicFile).size
    }

    @JvmStatic
    fun clear(world: World, lower: Vector, upper: Vector) {
        val air = BaseBlock(Material.AIR.id)
        val region: Region = CuboidRegion(BukkitWorld(world), lower, upper)
        try {
            newEditSession(world).setBlocks(region, air)
        } catch (ex: MaxChangedBlocksException) {

            
            throw RuntimeException(ex)
        }
    }

    @JvmStatic
    fun clear(world: World, bounds: Cuboid) {
        clear(
            world,
            Vector(bounds.lowerX, bounds.lowerY, bounds.lowerZ),
            Vector(bounds.upperX, bounds.upperY, bounds.upperZ)
        )
    }

}