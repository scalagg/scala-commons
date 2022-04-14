/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.buttons

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class AddButton : TexturedHeadButton(texture = HEAD_TEXTURE) {

    override fun getMaterial(player: Player): XMaterial
    {
        return XMaterial.SKELETON_SKULL
    }

    override fun getDamageValue(player: Player): Byte {
        return 3.toByte()
    }

    override fun getButtonItem(player: Player): ItemStack {
        return ItemUtils.applySkullTexture(super.getButtonItem(player), HEAD_TEXTURE)
    }

    companion object {
        private const val HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19"
    }

}
