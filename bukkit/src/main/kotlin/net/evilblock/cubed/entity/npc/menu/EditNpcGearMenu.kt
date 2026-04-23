package net.evilblock.cubed.entity.npc.menu

import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.StaticItemStackButton
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class EditNpcGearMenu(private val npc: NpcEntity) : Menu() {

    companion object {
        private val GLASS_SLOTS = listOf(
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 11, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26
        )
    }

    init {
        updateAfterClick = false
    }

    override fun getTitle(player: Player): String {
        return "Edit Gear - ${npc.getLines().first()}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            if (npc.itemInHand != null) {
                buttons[10] = StaticItemStackButton(npc.itemInHand!!.clone())
            }

            if (npc.helmet != null) {
                buttons[12] = StaticItemStackButton(npc.helmet!!.clone())
            }

            if (npc.chestplate != null) {
                buttons[13] = StaticItemStackButton(npc.chestplate!!.clone())
            }

            if (npc.leggings != null) {
                buttons[14] = StaticItemStackButton(npc.leggings!!.clone())
            }

            if (npc.boots != null) {
                buttons[15] = StaticItemStackButton(npc.boots!!.clone())
            }

            for (i in GLASS_SLOTS) {
                buttons[i] = GlassButton(7)
            }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 27
    }

    override fun acceptsInsertedItem(player: Player, itemStack: ItemStack, slot: Int): Boolean {
        return when (slot) {
            10 -> {
                npc.updateItemInHand(itemStack.clone())
                true
            }
            12 -> {
                npc.updateHelmet(itemStack.clone())
                true
            }
            13 -> {
                npc.updateChestplate(itemStack.clone())
                true
            }
            14 -> {
                npc.updateLeggings(itemStack.clone())
                true
            }
            15 -> {
                npc.updateBoots(itemStack.clone())
                true
            }
            else -> false
        }
    }

}
