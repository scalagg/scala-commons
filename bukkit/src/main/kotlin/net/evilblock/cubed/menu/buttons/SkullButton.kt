/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.buttons

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.menu.Button
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

open class SkullButton(private val owner: UUID) : Button() {

    override fun getMaterial(player: Player): Material {
        return Material.SKULL_ITEM
    }

    override fun getDamageValue(player: Player): Byte {
        return 3.toByte()
    }

    override fun getButtonItem(player: Player): ItemStack {
        val item = super.getButtonItem(player)

        if (item.hasItemMeta() && item.itemMeta is SkullMeta) {
            val meta = item.itemMeta as SkullMeta
            meta.owner = Cubed.instance.uuidCache.name(owner)
            item.itemMeta = meta
        }

        return item
    }

}