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
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView

class JumpToPageButton(
    private val page: Int,
    private val menu: PaginatedMenu,
    private val current: Boolean
) : Button() {

    override fun getName(player: Player): String {
        return "${CC.B_YELLOW}Page #$page"
    }

    override fun getDescription(player: Player): List<String> {
        val description = mutableListOf<String>()
        description += "${CC.GRAY}Click to open."

        if (current)
        {
            description += ""
            description += "${CC.GREEN}You're on this page!"
        }

        return description
    }

    override fun getMaterial(player: Player): XMaterial
    {
        return if (current) XMaterial.ENCHANTED_BOOK else XMaterial.BOOK
    }

    override fun getAmount(player: Player): Int {
        return this.page
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (!current) {
            menu.modPage(player, page - menu.page)
            playNeutral(player)
        }
    }
}
