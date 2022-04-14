/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.buttons

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.menu.Button
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

open class SkullButton(private val owner: String) : Button() {

    override fun getMaterial(player: Player): XMaterial
    {
        return XMaterial.SKELETON_SKULL
    }

    override fun getDamageValue(player: Player): Byte {
        return 3.toByte()
    }

    override fun getButtonItem(player: Player): ItemStack {
        val item = super.getButtonItem(player)

        if (item.hasItemMeta() && item.itemMeta is SkullMeta) {
            val meta = item.itemMeta as SkullMeta
            meta.owner = owner
            item.itemMeta = meta
        }

        return item
    }

}
