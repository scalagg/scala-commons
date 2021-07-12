/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.pagination

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class PageButton(private val mod: Int, private val menu: PaginatedMenu) : Button() {

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        when {
            clickType == ClickType.RIGHT && hasNext(player) -> {
                ViewAllPagesMenu(menu).openMenu(player)
                playNeutral(player)
            }
            hasNext(player) -> {
                menu.modPage(player, mod)
                playNeutral(player)
            }
//            else -> playFail(player)
        }
    }

    private fun hasNext(player: Player): Boolean {
        val pg = menu.page + mod
        return pg > 0 && menu.getPages(player) >= pg
    }

    override fun getName(player: Player): String {
        if (!hasNext(player)) {
            return " "
        }

        return (if (mod > 0) CC.GREEN + "Next page" else CC.RED + "Previous page") + ChatColor.GRAY + " (" + CC.PRI + (menu.page + mod) + CC.GRAY + "/" + CC.PRI + menu.getPages(player) + CC.GRAY + ")"
    }

    override fun getDescription(player: Player): List<String> {
        return if (hasNext(player)) listOf(CC.GRAY + "Right Click to view all pages!") else ArrayList()
    }

    override fun getDamageValue(player: Player): Byte {
        return if (hasNext(player)) 3.toByte() else 7.toByte()
    }

    override fun getMaterial(player: Player): Material {
        return if (hasNext(player)) Material.SKULL_ITEM else Material.STAINED_GLASS_PANE
    }

    override fun getButtonItem(player: Player): ItemStack {
        if (!hasNext(player)) {
            return super.getButtonItem(player)
        }

        val texture = if (menu.verticalView) {
            if (mod >= 1) {
                Constants.WOOD_ARROW_DOWN_TEXTURE
            } else {
                Constants.WOOD_ARROW_UP_TEXTURE
            }
        } else {
            if (mod >= 1) {
                Constants.WOOD_ARROW_RIGHT_TEXTURE
            } else {
                Constants.WOOD_ARROW_LEFT_TEXTURE
            }
        }

        return ItemUtils.applySkullTexture(super.getButtonItem(player), texture)
    }

}