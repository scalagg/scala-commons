

package net.evilblock.cubed.menu

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.menu.distribution.MenuRowDistribution
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.nms.MinecraftReflection
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.math.ceil

abstract class Menu
{
    var buttons: ConcurrentHashMap<Int, Button> = ConcurrentHashMap()

    var autoUpdate: Boolean = false
    var autoUpdateInterval: Long = 500L

    var animated: Boolean = false
    var updateAfterClick: Boolean = false
    var keepBottomMechanics: Boolean = true

    var placeholder: Boolean = false
    var placeholdBorders: Boolean = false

    var nonCancelling: Boolean = false
    var async: Boolean = false
    var manualClose: Boolean = true
    var closed: Boolean = false

    var inventoryType = InventoryType.CHEST
    var distribution = MenuRowDistribution.NONE

    private var staticTitle: String

    constructor()
    {
        staticTitle = " "
    }

    constructor(title: String)
    {
        staticTitle = title
    }

    abstract fun getButtons(player: Player): Map<Int, Button>

    open fun getTitleContextual(player: Player, items: Int): String
    {
        return getTitle(player)
    }

    open fun getTitle(player: Player): String
    {
        return staticTitle
    }

    open fun onOpen(player: Player)
    {
    }

    open fun onClose(player: Player, manualClose: Boolean)
    {
    }

    private fun createInventory(player: Player): Inventory
    {
        val invButtons = getButtons(player)
        var size = size(invButtons)

        var title = ChatColor
            .translateAlternateColorCodes(
                '&',
                getTitleContextual(player, -1)
            )

        if (title.length > 32)
        {
            title = title.substring(0, 31)
        }

        val minSize = getMinSize()
        if (minSize != -1)
        {
            if (minSize > size)
            {
                size = minSize
            }
        }

        val inv = if (inventoryType == InventoryType.CHEST)
        {
            Bukkit.createInventory(null, size, title)
        } else
        {
            Bukkit.createInventory(null, inventoryType, title)
        }

        for (buttonEntry in invButtons.entries)
        {
            if (buttonEntry.key >= size)
            {
                continue
            }

            buttons[buttonEntry.key] = buttonEntry.value
            inv.setItem(buttonEntry.key, buttonEntry.value.getButtonItem(player))
        }

        val current = size(invButtons)

        if (placeholdBorders)
        {
            val placeholder = Button.placeholder(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()!!, 15.toByte(), " ")

            for (i in 0 until current)
            {
                if (i < 9 || i >= current - 9 || i % 9 == 0 || i % 9 == 8)
                {
                    if (invButtons[i] == null)
                    {
                        inv.setItem(i, placeholder.getButtonItem(player))
                    }
                }
            }
        }

        if (placeholder)
        {
            val placeholder = Button.placeholder(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()!!, 15.toByte(), " ")

            for (index in 0 until size(invButtons))
            {
                if (invButtons[index] == null)
                {
                    buttons[index] = placeholder
                    inv.setItem(index, placeholder.getButtonItem(player))
                }
            }
        }

        return inv
    }

    fun openMenu(player: Player)
    {
        if (async)
        {
            Tasks.async {
                try
                {
                    asyncLoadResources(player) { successfulLoad ->
                        if (successfulLoad)
                        {
                            val inv = createInventory(player)

                            try
                            {
                                openCustomInventory(player, inv)
                            } catch (ex: Exception)
                            {
                                ex.printStackTrace()
                            }
                        } else
                        {
                            player.sendMessage("${ChatColor.RED}Couldn't load menu...")
                        }
                    }
                } catch (e: Exception)
                {
                    e.printStackTrace()
                    player.sendMessage("${ChatColor.RED}I'm sorry, but we weren't able to open this GUI for you.")
                }
            }
        } else
        {
            val opener = {
                kotlin.runCatching {
                    openCustomInventory(
                        player, createInventory(player)
                    )
                }.onFailure {
                    it.printStackTrace()
                }

                Unit
            }

            if (MinecraftReflection.isModern())
            {
                Tasks.sync(opener)
            } else
            {
                opener()
            }
        }
    }

    private fun openCustomInventory(
        player: Player, inventory: Inventory
    )
    {
        val entityPlayer = MinecraftReflection.getHandle(player)
        val openInventory = player.openInventory

        if (openInventory != null)
        {
            if (
                openInventory.topInventory != null &&
                openInventory.topInventory.size == inventory.size
            )
            {
                if (
                    !MinecraftReflection.isModern() &&
                    openInventory.topInventory.title == inventory.title
                )
                {
                    openInventory.topInventory.contents = inventory.contents
                    return
                }
            }

            manualClose = false
        }

        val inventoryOpener = {
            if (MinecraftReflection.isModern() || System.getenv("gg.scala.commons.menu.NoCustomInventories") != null)
            {
                player.openInventory(inventory)
            } else
            {
                OPEN_CUSTOM_INVENTORY_METHOD
                    ?.invoke(
                        player, inventory, entityPlayer, getWindowType(inventory.size)
                    )
                    ?: player.openInventory(inventory)
            }

            update(player)

            if (player.openInventory != null)
            {
                player.updateInventory()
            }
        }

        if (Bukkit.isPrimaryThread())
        {
            inventoryOpener()
        } else
        {
            Tasks.sync {
                inventoryOpener()
            }
        }
    }

    private val characters = ('a'..'f').toList()

    val reflection = Reflection.getClassSuppressed(
        "net.minecraft.world.inventory.Containers"
    )

    private val containerMappings =
        mutableMapOf<Char, Any>().apply {
            if (reflection == null)
                return@apply

            for (character in characters)
            {
                val container = reflection
                    .getDeclaredField(
                        character.toString()
                    )
                    .get(null)
                    ?: continue

                this[character] = container
            }
        }

    private val inventoryTypeMappings = mapOf(
        InventoryType.CHEST to "minecraft:chest",
        InventoryType.PLAYER to "minecraft:chest",
        InventoryType.ENDER_CHEST to "minecraft:chest",
        InventoryType.DISPENSER to "minecraft:dispenser",
        InventoryType.DROPPER to "minecraft:dropper",
        InventoryType.FURNACE to "minecraft:furnace",
        InventoryType.WORKBENCH to "minecraft:crafting_table",
        InventoryType.BREWING to "minecraft:brewing_stand",
        InventoryType.ENCHANTING to "minecraft:enchanting_table",
        InventoryType.HOPPER to "minecraft:hopper",
        InventoryType.BEACON to "minecraft:beacon",
        InventoryType.ANVIL to "minecraft:anvil"
    )

    private fun getWindowType(size: Int): Any
    {
        if (!MinecraftReflection.isModern())
        {
            return inventoryTypeMappings[inventoryType]!!
        }

        val sizeRow = (size / 9) - 1
        val mapping = characters[sizeRow]

        return containerMappings[mapping]!!
    }

    fun update(player: Player)
    {

        currentlyOpenedMenus[player.uniqueId] = this

        closed = false
        onOpen(player)
    }

    open fun size(buttons: Map<Int, Button>): Int
    {
        var highest = 0
        for (buttonValue in buttons.keys)
        {
            if (buttonValue > highest)
            {
                highest = buttonValue
            }
        }
        return (ceil((highest + 1) / 9.0) * 9.0).toInt()
    }

    open fun getMinSize(): Int
    {
        return -1
    }

    fun getSlot(x: Int, y: Int): Int
    {
        return 9 * y + x
    }

    open fun asyncLoadResources(player: Player, callback: (Boolean) -> Unit)
    {
    }

    open fun acceptsInsertedItem(player: Player, itemStack: ItemStack, slot: Int): Boolean
    {
        return false
    }

    open fun acceptsShiftClickedItem(player: Player, itemStack: ItemStack): Boolean
    {
        return false
    }

    open fun acceptsDraggedItems(player: Player, items: Map<Int, ItemStack>): Boolean
    {
        return false
    }

    open fun getAutoUpdateTicks(): Long
    {
        return autoUpdateInterval
    }

    open fun preAutoUpdate()
    {
    }

    companion object
    {
        @JvmStatic
        var currentlyOpenedMenus: ConcurrentHashMap<UUID, Menu> = ConcurrentHashMap()

        private val OPEN_CUSTOM_INVENTORY_METHOD = kotlin
            .runCatching {
                Reflection
                    .getDeclaredMethod(
                        MinecraftReflection.getCraftBukkitClass("entity.CraftHumanEntity")!!,
                        "openCustomInventory",
                        Inventory::class.java,
                        MinecraftReflection.getNMSClass(
                            "${
                                if (MinecraftReflection.isModern())
                                    "level." else ""
                            }EntityPlayer"
                        )!!,
                        if (MinecraftReflection.isModern())
                        {
                            Reflection.getClassSuppressed(
                                "net.minecraft.world.inventory.Containers"
                            )!!
                        } else
                        {
                            String::class.java
                        }
                    )
            }
            .onFailure {
                Logger.getGlobal().log(
                    Level.SEVERE, "Failed to create custom inventory services", it
                )
            }
            .getOrNull()
    }
}
