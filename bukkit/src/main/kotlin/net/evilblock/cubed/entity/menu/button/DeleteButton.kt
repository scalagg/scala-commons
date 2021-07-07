package net.evilblock.cubed.entity.menu.button

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.protocol.MenuCompatibility
import net.evilblock.cubed.util.text.TextSplitter
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class DeleteButton(private val entity: Entity) : Button() {

    override fun getName(player: Player): String {
        return "${ChatColor.RED}${ChatColor.BOLD}Delete ${entity.getTypeName()}"
    }

    override fun getDescription(player: Player): List<String> {
        return arrayListOf<String>().also { desc ->
            desc.addAll(TextSplitter.split(text = "Delete this ${entity.getTypeName()}."))
            desc.add("")
            desc.add("${ChatColor.RED}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.RED}to delete ${entity.getTypeName().toLowerCase()}")
        }
    }

    override fun getMaterial(player: Player): Material {
        return MenuCompatibility.getBarrierOrReplacement()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        ConfirmMenu("Delete ${entity.getTypeName()}?") { value ->
            player.closeInventory()

            if (value) {
                entity.destroyForCurrentWatchers()

                EntityHandler.forgetEntity(entity)

                player.sendMessage("${ChatColor.GREEN}Deleted ${entity.getTypeName()}!")
            } else {
                player.sendMessage("${ChatColor.YELLOW}No changes made to ${entity.getTypeName()}.")
            }
        }.openMenu(player)
    }

}