/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.pagination

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.nms.MinecraftReflection
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class PageButton(
    private val mod: Int,
    private val menu: PaginatedMenu,
    private val pages: Int
) : Button() {

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        val hasNext = hasNext(player)

        if (
            clickType.name.contains("SHIFT") &&
            hasNext && !menu.verticalView
        )
        {
            ViewAllPagesMenu(menu, pages = this.pages).openMenu(player)
            playNeutral(player)
            return
        }

        if (hasNext)
        {
            menu.modPage(player, mod)
            playNeutral(player)
        }
    }

    private fun hasNext(player: Player): Boolean {
        val pg = menu.page + mod
        return pg in 1..pages
    }

    override fun getName(player: Player): String {
        if (!hasNext(player)) {
            return " "
        }

        return (if (mod > 0) CC.GREEN + (if (menu.verticalView) "Scroll down" else "Next page") else CC.RED + (if (menu.verticalView) "Scroll up" else "Previous page")) + ChatColor.GRAY + " (${menu.page + mod}/$pages)"
    }

    override fun getDescription(player: Player): List<String> {
        return if (hasNext(player) && !menu.verticalView)
            listOf(
                "${CC.WHITE}Shift-click to view all",
                "${CC.WHITE}available pages."
            ) else ArrayList()
    }

    override fun getDamageValue(player: Player): Byte {
        return if (hasNext(player)) 3.toByte() else 15.toByte()
    }

    override fun getMaterial(player: Player): XMaterial {
        if (MinecraftReflection.isModern())
        {
            return if (hasNext(player)) XMaterial.GLISTERING_MELON_SLICE else XMaterial.BLACK_STAINED_GLASS_PANE
        }

        return if (hasNext(player)) XMaterial.SKELETON_SKULL else XMaterial.BLACK_STAINED_GLASS_PANE
    }

    override fun getButtonItem(player: Player): ItemStack {
        if (MinecraftReflection.isModern())
        {
            return super.getButtonItem(player)
        }

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
