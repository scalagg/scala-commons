package net.evilblock.cubed.menu.buttons

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.menu.Button
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

/**
 * A button that closes the current menu.
 *
 * @author Limo
 * @date 2/10/2026
 */
class CloseButton : Button() {

    override fun getName(player: Player): String {
        return "${ChatColor.RED}Close"
    }

    override fun getDescription(player: Player): List<String> {
        return emptyList()
    }

    override fun getMaterial(player: Player): XMaterial {
        return XMaterial.BARRIER
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        playNeutral(player)
        player.closeInventory()
    }

}
