/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.tool

import com.cryptomorin.xseries.XMaterial
import gg.scala.commons.annotations.Listeners
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.npc.util.NpcUtils
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.AngleUtils
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.x
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.*
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * Handles bukkit events for Move Tool functionality and UX.
 */
@Listeners
object EntityMoveTool : Listener
{

    @JvmStatic
    fun makeMoveTool(entity: Entity): ItemStack
    {
        val item = ItemBuilder.of(XMaterial.VILLAGER_SPAWN_EGG)
            .data(120)
            .name("${CC.GREEN}Move ${entity.getTypeName()}")
            .setLore(arrayListOf<String>().also { desc ->
                desc.add("${CC.GRAY}Place this mob egg wherever")
                desc.add("${CC.GRAY}you'd like to move the NPC.")
            })
            .build()

        return ItemUtils.setMonsterEggType(item, EntityType.VILLAGER)
    }

    /**
     * If the given [itemStack] is a move tool.
     */
    @JvmStatic
    fun isMoveTool(itemStack: ItemStack?, player: Player): Boolean
    {
        if (itemStack == null)
        {
            return false
        }

        if (itemStack.type.x() != XMaterial.VILLAGER_SPAWN_EGG)
        {
            return false
        }

        if (player.hasMetadata("moving"))
        {
            return true
        }

        return false
    }

    @JvmStatic
    fun clearInventoryOfTool(player: Player)
    {
        for (i in 0 until 36)
        {
            val itemStack = player.inventory.contents[i]

            if (isMoveTool(itemStack, player))
            {
                val uuid = player
                    .getMetadata("moving")
                    .firstOrNull()
                    ?: return

                EntityHandler
                    .getEntityByUuid(
                        UUID.fromString(
                            uuid.asString()
                        )
                    )
                    ?.updateVisibility(false)

                player.inventory.contents[i] = null
                player.inventory.setItem(i, null)
                player.updateInventory()
            }
        }
    }

    /**
     * Handles move tool functionality.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerInteractEvent(event: PlayerInteractEvent)
    {
        if (event.action == Action.RIGHT_CLICK_BLOCK)
        {
            val itemInHand = event.player.inventory.itemInHand

            if (isMoveTool(itemInHand, event.player))
            {
                event.isCancelled = true

                val uuid = event.player
                    .getMetadata("moving")
                    .firstOrNull()
                    ?: return

                val entity = EntityHandler
                    .getEntityByUuid(
                        UUID.fromString(
                            uuid.asString()
                        )
                    )

                if (entity != null && event.clickedBlock != null)
                {
                    // npc direction == flipped player yaw
                    val direction = event.player.eyeLocation.direction.multiply(-1)
                    val height = NpcUtils.BLOCK_HEIGHTS.getOrDefault(event.clickedBlock!!.type.name, 1.0)

                    val updatedLocation = event.clickedBlock!!.location.add(0.5, height, 0.5)
                    updatedLocation.direction = direction

                    val directionFace = AngleUtils.yawToFace(updatedLocation.yaw)
                    updatedLocation.yaw = AngleUtils.faceToYaw(directionFace).toFloat()
                    updatedLocation.pitch = 0.0F

                    // check with other entity implementations
                    val entityMovedEvent = EntityMovedEvent(event.player, entity, updatedLocation)
                    Bukkit.getServer().pluginManager.callEvent(entityMovedEvent)

                    if (entityMovedEvent.isCancelled)
                    {
                        return
                    }

                    entity.updateLocation(updatedLocation)
                    entity.updateVisibility(false)

                    event.player.inventory.itemInHand = ItemStack(Material.AIR)
                    event.player.updateInventory()
                    event.player.sendMessage("${CC.GREEN}Successfully updated location!")
                }
            }
        }
    }

    /**
     * Prevents any move tools from being inserted into a container via clicks.
     */
    @EventHandler
    fun onInventoryClickEvent(event: InventoryClickEvent)
    {
        if (
            event.action.name.contains("DROP") &&
            isMoveTool(event.currentItem, event.viewers.first() as Player)
        )
        {
            event.isCancelled = true
        }
    }

    /**
     * Prevents any move tools from being inserted into a container via drag.
     */
    @EventHandler
    fun onInventoryDragEvent(event: InventoryDragEvent)
    {
        if (event.cursor != null && isMoveTool(event.cursor, event.viewers.first() as Player))
        {
            event.isCancelled = true
            return
        }
    }

    /**
     * Prevents any move tools from being dropped due to a player death.
     */
    @EventHandler
    fun onPlayerDeathEvent(event: PlayerDeathEvent)
    {
        val dropIterator = event.drops.iterator()
        while (dropIterator.hasNext())
        {
            val drop = dropIterator.next()

            if (isMoveTool(drop, event.entity))
            {
                dropIterator.remove()
            }
        }
    }

    /**
     * Prevents any move tools from being dropped.
     */
    @EventHandler
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent)
    {
        if (isMoveTool(event.itemDrop.itemStack, event.player))
        {
            event.isCancelled = true

            Tasks.delayed(1L) {
                clearInventoryOfTool(event.player)
            }
        }
    }

    /**
     * Removes any move tools from the player's inventory when they logout.
     *
     * If any move tools are found, the entity is extracted from the item metadata
     * and the entity's visibility is updated to visible to prevent any NPCs
     * from becoming "lost".
     */
    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent)
    {
        clearInventoryOfTool(event.player)
    }

    class EntityMovedEvent(val player: Player, val entity: Entity, val location: Location) : Event(), Cancellable
    {
        private var cancelled: Boolean = false

        override fun isCancelled(): Boolean
        {
            return cancelled
        }

        override fun setCancelled(cancel: Boolean)
        {
            this.cancelled = cancel
        }

        companion object
        {
            @JvmStatic
            val handlerList = HandlerList()
        }

        override fun getHandlers(): HandlerList
        {
            return handlerList
        }
    }

}

