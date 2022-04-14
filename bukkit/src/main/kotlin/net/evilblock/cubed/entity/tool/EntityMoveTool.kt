/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.tool

import me.lucko.helper.shadows.nbt.NBTTagString
import me.lucko.shadow.ShadowFactory
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.npc.util.NpcUtils
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.AngleUtils
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
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
object EntityMoveTool : Listener {

    @JvmStatic
    fun makeMoveTool(entity: Entity): ItemStack {
        val item = ItemBuilder.of(Material.MONSTER_EGG)
            .data(120)
            .name("${CC.GREEN}Move ${entity.getTypeName()}")
            .setLore(arrayListOf<String>().also { desc ->
                desc.add("${CC.GRAY}Place this mob egg wherever")
                desc.add("${CC.GRAY}you'd like to move the NPC.")
            })
            .build()

        return ItemUtils.addToItemTag(ItemUtils.setMonsterEggType(item, EntityType.VILLAGER), "EntityMoveTool", entity.uuid.toString())
    }

    /**
     * If the given [itemStack] is a move tool.
     */
    @JvmStatic
    fun isMoveTool(itemStack: ItemStack?): Boolean {
        if (itemStack == null) {
            return false
        }

        if (itemStack.type != Material.MONSTER_EGG) {
            return false
        }

        if (ItemUtils.itemTagHasKey(itemStack, "EntityMoveTool")) {
            return true
        }

        return false
    }

    @JvmStatic
    fun clearInventoryOfTool(player: Player) {
        for (i in 0 until 36) {
            val itemStack = player.inventory.contents[i]
            if (isMoveTool(itemStack)) {
                val uuid = UUID.fromString(
                    ShadowFactory.global()
                        .shadow(
                            NBTTagString::class.java,
                            ItemUtils.readItemTagKey(itemStack, "EntityMoveTool")
                        ).string
                )

                EntityHandler.getEntityByUuid(uuid)
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
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            val itemInHand = event.player.inventory.itemInHand
            if (itemInHand != null) {
                if (isMoveTool(itemInHand)) {
                    event.isCancelled = true

                    val uuid = UUID.randomUUID()
                    event.player.sendMessage("${ChatColor.GREEN}This functionality is temporailry disabled.")

                    val entity = EntityHandler.getEntityByUuid(uuid)
                    if (entity != null) {
                        // npc direction == flipped player yaw
                        val direction = event.player.eyeLocation.direction.multiply(-1)
                        val height = NpcUtils.BLOCK_HEIGHTS.getOrDefault(event.clickedBlock.type, 1.0)

                        val updatedLocation = event.clickedBlock.location.add(0.5, height, 0.5)
                        updatedLocation.direction = direction

                        val directionFace = AngleUtils.yawToFace(updatedLocation.yaw)
                        updatedLocation.yaw = AngleUtils.faceToYaw(directionFace).toFloat()
                        updatedLocation.pitch = 0.0F

                        // check with other entity implementations
                        val entityMovedEvent = EntityMovedEvent(event.player, entity, updatedLocation)
                        Bukkit.getServer().pluginManager.callEvent(entityMovedEvent)

                        if (entityMovedEvent.isCancelled) {
                            return
                        }

                        entity.updateLocation(updatedLocation)
                        entity.updateVisibility(false)

                        event.player.inventory.itemInHand = null
                        event.player.updateInventory()
                        event.player.sendMessage("${CC.GREEN}Successfully updated location!")
                    }
                }
            }
        }
    }

    /**
     * Prevents any move tools from being inserted into a container via clicks.
     */
    @EventHandler
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        if (event.action.name.contains("DROP") && isMoveTool(event.currentItem)) {
            event.isCancelled = true
        }
    }

    /**
     * Prevents any move tools from being inserted into a container via drag.
     */
    @EventHandler
    fun onInventoryDragEvent(event: InventoryDragEvent) {
        if (event.cursor != null && isMoveTool(event.cursor)) {
            event.isCancelled = true
            return
        }
    }

    /**
     * Prevents any move tools from being dropped due to a player death.
     */
    @EventHandler
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        val dropIterator = event.drops.iterator()
        while (dropIterator.hasNext()) {
            val drop = dropIterator.next()
            if (isMoveTool(drop)) {
                dropIterator.remove()
            }
        }
    }

    /**
     * Prevents any move tools from being dropped.
     */
    @EventHandler
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        if (isMoveTool(event.itemDrop.itemStack)) {
            event.isCancelled = true

            Cubed.instance.server.scheduler.runTaskLater(Cubed.instance, {
                clearInventoryOfTool(event.player)
            }, 1L)
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
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        clearInventoryOfTool(event.player)
    }

    class EntityMovedEvent(val player: Player, val entity: Entity, val location: Location) : Event(), Cancellable {
        private var cancelled: Boolean = false

        override fun isCancelled(): Boolean {
            return cancelled
        }

        override fun setCancelled(cancel: Boolean) {
            this.cancelled = cancel
        }

        companion object {
            @JvmStatic val handlerList = HandlerList()
        }

        override fun getHandlers(): HandlerList {
            return handlerList
        }
    }

}
