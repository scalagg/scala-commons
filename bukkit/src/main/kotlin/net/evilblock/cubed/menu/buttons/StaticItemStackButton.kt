package net.evilblock.cubed.menu.buttons

import net.evilblock.cubed.menu.Button
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class StaticItemStackButton(private val item: ItemStack) : Button() {

    override fun getButtonItem(player: Player): ItemStack {
        return item.clone()
    }

}