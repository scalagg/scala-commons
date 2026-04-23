package net.evilblock.cubed.entity.tool

import gg.scala.commons.annotations.Listeners
import gg.scala.flavor.inject.Inject
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.event.PlayerRightClickEntityEvent
import net.evilblock.cubed.entity.hologram.HologramEntity
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.metadata.FixedMetadataValue

@Listeners
object EntitySelectionTool : Listener
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    @JvmStatic
    fun isSelecting(player: Player): Boolean
    {
        return player.hasMetadata("ENTITY_SELECTION")
    }

    @JvmStatic
    fun attachSelectionHandler(player: Player, lambda: (Entity) -> Unit)
    {
        player.setMetadata("ENTITY_SELECTION", FixedMetadataValue(plugin, lambda))
    }

    @JvmStatic
    fun handleSelection(player: Player, entity: Entity)
    {
        val resolvedEntity = resolveSelectionEntity(entity)

        // first get the callback func
        val callbackFunction = player.getMetadata("ENTITY_SELECTION")[0].value() as (Entity) -> Unit

        // then remove the metadata, that way we can assign another entity selection
        // handler from within our callback func
        player.removeMetadata("ENTITY_SELECTION", plugin)

        // invoke our callback func
        callbackFunction.invoke(resolvedEntity)
    }

    private fun resolveSelectionEntity(entity: Entity): Entity
    {
        var current = entity

        while (current is HologramEntity && current.parent != null)
        {
            current = current.parent!!
        }

        return current
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerRightClickEntityEvent(event: PlayerRightClickEntityEvent)
    {
        if (isSelecting(event.player))
        {
            event.isCancelled = true

            handleSelection(event.player, event.entity)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerInteractEvent(event: PlayerInteractEvent)
    {
        val player = event.player
        if (!isSelecting(player))
        {
            return
        }

        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK)
        {
            return
        }

        val eye = player.eyeLocation.toVector()
        val direction = player.eyeLocation.direction.normalize()

        val selected = EntityHandler.getEntities()
            .asSequence()
            .filter { entity ->
                entity.location.world == player.world &&
                    entity.location.distanceSquared(player.location) <= 256.0
            }
            .map { entity ->
                resolveSelectionEntity(entity)
            }
            .distinctBy { entity -> entity.uuid }
            .map { entity ->
                val targetLocation = when (entity)
                {
                    is HologramEntity -> entity.getDebugViewLocation()
                    else -> entity.location
                }

                val toEntity = targetLocation.toVector().subtract(eye)
                val projection = toEntity.dot(direction)
                val perpendicular = toEntity.clone().subtract(direction.clone().multiply(projection)).lengthSquared()
                Triple(entity, projection, perpendicular)
            }
            .filter { (_, projection, perpendicular) ->
                projection > 0.0 && projection <= 16.0 && perpendicular <= 2.25
            }
            .sortedWith(compareBy<Triple<Entity, Double, Double>> { it.third }.thenBy { it.second })
            .firstOrNull()
            ?.first
            ?: return

        event.isCancelled = true
        Bukkit.getScheduler().runTask(plugin) {
            if (isSelecting(player))
            {
                handleSelection(player, selected)
            }
        }
    }

}
