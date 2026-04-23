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

abstract class RemoveButton : TexturedHeadButton(texture = HEAD_TEXTURE) {

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
        private const val HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0="
    }

}

/**
 * Creates a concrete [RemoveButton] with the given click handler.
 */
@Suppress("FunctionName")
fun RemoveButton(onClick: () -> Unit): RemoveButton = object : RemoveButton() {
    override fun clicked(player: Player, slot: Int, clickType: org.bukkit.event.inventory.ClickType, view: org.bukkit.inventory.InventoryView) {
        onClick()
    }
}
