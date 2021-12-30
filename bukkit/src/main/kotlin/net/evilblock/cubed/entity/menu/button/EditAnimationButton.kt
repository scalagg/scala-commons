package net.evilblock.cubed.entity.menu.button

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.menu.ApplyAnimationMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.text.TextSplitter
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class EditAnimationButton(private val entity: Entity) : Button() {

    override fun getName(player: Player): String {
        return "${CC.GREEN}Apply Animation"
    }

    override fun getDescription(player: Player): List<String> {
        return arrayListOf<String>().also { desc ->
            desc.addAll(TextSplitter.split(text = "Apply an animation to this entity."))
            desc.add("")
            desc.add("${CC.GREEN}Left-Click to apply animation.")

            if (entity.animation != null) {
                desc.add("${CC.RED}Right-Click to remove animation.")
            }
        }
    }

    override fun getMaterial(player: Player): Material {
        return Material.BLAZE_POWDER
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (clickType.isLeftClick) {
            ApplyAnimationMenu(entity) { animation ->
                entity.animation = animation

                Tasks.async {
                    EntityHandler.saveData()
                }
            }.openMenu(player)
        } else if (clickType.isRightClick && entity.animation != null) {
            val openMenu = Menu.currentlyOpenedMenus[player.uniqueId] ?: return

            ConfirmMenu { confirmed ->
                if (confirmed) {
                    entity.animation = null

                    Tasks.async {
                        EntityHandler.saveData()
                    }

                    openMenu.openMenu(player)
                }
            }.openMenu(player)
        }
    }

}
