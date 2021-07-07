/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.bukkit

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object InventoryUtils {

    @JvmStatic
    fun addAmountToInventory(inventory: Inventory, itemStack: ItemStack, amount: Int): ItemStack? {
        return changeInventoryAmount(inventory.contents, itemStack, amount, true)
    }

//    @JvmStatic
//    fun removeAmountFromInventory(inventory: Inventory, itemStack: ItemStack, amount: Int) {
//        changeInventoryAmount(inventory.contents, itemStack, amount, false)
//    }

    @JvmStatic
    fun addAmountToInventory(inventory: Array<ItemStack?>, itemStack: ItemStack, amount: Int): ItemStack? {
        return changeInventoryAmount(inventory, itemStack, amount, true)
    }

//    @JvmStatic
//    fun removeAmountFromInventory(inventory: Array<ItemStack?>, itemStack: ItemStack, amount: Int) {
//        changeInventoryAmount(inventory, itemStack, amount, false)
//    }

    /**
     * Finds the first entry in the given [inventory] that is null.
     *
     * @return The first index whose entry in the [inventory] is null, or -1.
     */
    private fun firstEmpty(inventory: Array<ItemStack?>): Int {
        for (i in inventory.indices) {
            if (inventory[i] == null) {
                return i
            }
        }
        return -1
    }

    /**
     * Tries to insert the given [itemStack] to the given [inventory]. The given [itemStack] will try and be stacked onto
     * existing slots in the [inventory] whose items are similar to the [itemStack] and can fit more in their stacks.
     *
     * First we try and stack, then use an empty slot if there is still a remaining amount to be inserted. Essentially we
     * are mocking how the Minecraft Inventory UI works.
     */
    private fun changeInventoryAmount(inventory: Array<ItemStack?>, itemStack: ItemStack, amount: Int, add: Boolean): ItemStack? {
        var remainingAmount = amount

        for (i in inventory.indices) {
            val itemAtSlot = inventory[i] ?: continue

            if (itemAtSlot.amount >= itemAtSlot.maxStackSize) {
                continue
            }

            if (!ItemUtils.isSimilar(itemAtSlot, itemStack) || !ItemUtils.hasSameLore(itemAtSlot, itemStack) || !ItemUtils.hasSameEnchantments(itemAtSlot, itemStack)) {
                continue
            }

            val maxInsert = itemAtSlot.maxStackSize - itemAtSlot.amount
            if (maxInsert <= 0) {
                continue
            }

            if (remainingAmount <= maxInsert) {
                itemAtSlot.amount = itemAtSlot.amount + remainingAmount
                remainingAmount = 0
            } else {
                itemAtSlot.amount = itemAtSlot.amount + maxInsert
                remainingAmount -= maxInsert
            }

            if (remainingAmount <= 0) {
                break
            }
        }

        if (remainingAmount > 0) {
            val firstEmpty = firstEmpty(inventory)
            if (firstEmpty != -1) {
                inventory[firstEmpty] = ItemBuilder.copyOf(itemStack).amount(remainingAmount.coerceAtMost(itemStack.type.maxStackSize)).build()
                remainingAmount -= remainingAmount.coerceAtMost(itemStack.type.maxStackSize)
            }
        }

        return if (remainingAmount > 0) {
            return ItemBuilder.copyOf(itemStack).amount(remainingAmount).build()
        } else {
            null
        }
    }

    @JvmStatic
    fun getMaxItemsSize(player: Player, matchingItem: ItemStack? = null): Int {
        return getMaxItemsSize(player.inventory, matchingItem)
    }

    @JvmStatic
    fun getMaxItemsSize(inventory: Inventory, matchingItem: ItemStack? = null): Int {
        var fits = 0
        for (item in inventory.contents) {
            if (item == null || item.type == Material.AIR) {
                fits += matchingItem?.maxStackSize ?: 64
            } else {
                if (matchingItem != null && ItemUtils.isSimilar(item, matchingItem)) {
                    fits += matchingItem.maxStackSize - item.amount
                }
            }
        }
        return fits
    }

    @JvmStatic
    fun getItemsPresent(inventory: Inventory, itemStack: ItemStack): Int {
        var present = 0
        for (item in inventory.contents) {
            if (item != null && ItemUtils.isSimilar(item, itemStack)) {
                present += item.amount
            }
        }
        return present
    }

}