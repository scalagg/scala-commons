package net.evilblock.cubed.entity.menu

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.animation.EntityAnimation
import net.evilblock.cubed.entity.animation.EntityAnimationType
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class ApplyAnimationMenu(private val entity: Entity, private val use: (EntityAnimation) -> Unit) : Menu() {

    override fun getTitle(player: Player): String {
        return "Apply Animation"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (type in entity.getAnimationTypes()) {
                buttons[buttons.size] = AnimationButton(type)
            }
        }
    }

    private inner class AnimationButton(private val type: EntityAnimationType) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}${type.getName()}"
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder.copyOf(type.getIcon()).name(getName(player)).build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                type.startProcedure(player, entity) { animation ->
                    use.invoke(animation)
                }
            }
        }
    }

}