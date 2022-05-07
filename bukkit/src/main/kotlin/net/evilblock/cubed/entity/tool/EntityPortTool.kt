package net.evilblock.cubed.entity.tool

import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import gg.scala.commons.annotations.Listeners
import gg.scala.flavor.inject.Inject
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Listeners
object EntityPortTool : Listener
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    private val FILE_NAME_REGEX = "[\\w,\\s-]+(\\.[A-Za-z]{3})?".toRegex()

    private var tracked: MutableMap<UUID, MutableSet<Entity>> = ConcurrentHashMap()

    fun isTracking(player: Player): Boolean
    {
        return tracked.containsKey(player.uniqueId)
    }

    fun startTracking(player: Player)
    {
        tracked[player.uniqueId] = hashSetOf()
    }

    fun stopTracking(player: Player)
    {
        tracked.remove(player.uniqueId)
    }

    fun getTrackedEntities(player: Player): Set<Entity>
    {
        return tracked[player.uniqueId]!!
    }

    fun isTrackingEntity(player: Player, entity: Entity): Boolean
    {
        return tracked[player.uniqueId]!!.contains(entity)
    }

    fun addTrackedEntity(player: Player, entity: Entity): Boolean
    {
        return tracked[player.uniqueId]!!.add(entity)
    }

    fun removeTrackedEntity(player: Player, entity: Entity): Boolean
    {
        return tracked[player.uniqueId]!!.remove(entity)
    }

    private fun getImportDirectory(): File
    {
        return File(plugin.dataFolder, "import")
    }

    fun findImportDataFile(fileName: String): File
    {
        return File(getImportDirectory(), fileName)
    }

    fun import(file: File): Set<Entity>
    {
        return Files.newReader(file, Charsets.UTF_8).use { reader ->
            Serializers.gson.fromJson(reader, object : TypeToken<Set<Entity>>()
            {}.type) as Set<Entity>
        }
    }

    private fun getExportDirectory(): File
    {
        return File(plugin.dataFolder, "export")
    }

    fun export(set: Set<Entity>, file: File)
    {
        file.parentFile.mkdirs()
        Files.write(Serializers.gson.toJson(set, object : TypeToken<Set<Entity>>()
        {}.type), file, Charsets.UTF_8)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerInteractEvent(event: PlayerInteractEvent)
    {
        if (event.player.hasPermission("commons.entity.export"))
        {
            if (isTracking(event.player))
            {
                if ((event.action == Action.RIGHT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_AIR) && event.player.isSneaking)
                {
                    ConfirmMenu("Export entities?") { confirmed ->
                        if (confirmed)
                        {
                            EzPrompt.Builder()
                                .promptText("${ChatColor.GREEN}What file name would you like to export to?")
                                .regex(FILE_NAME_REGEX)
                                .acceptInput { input ->
                                    Tasks.async {
                                        val fileName = if (!input.endsWith(".json", ignoreCase = true))
                                        {
                                            input
                                        } else
                                        {
                                            "$input.json"
                                        }

                                        val outputFile = File(getExportDirectory(), fileName)
                                        if (outputFile.exists())
                                        {
                                            event.player.sendMessage("${ChatColor.RED}That file already exists!")
                                            return@async
                                        }

                                        val markedForExport = tracked[event.player.uniqueId]!!
                                        export(markedForExport, outputFile)

                                        event.player.sendMessage("${ChatColor.GREEN}Successfully exported ${markedForExport.size} entities!")

                                        stopTracking(event.player)
                                    }
                                }
                                .build()
                                .start(event.player)
                        }
                    }.openMenu(event.player)
                }
            }
        }
    }

}
