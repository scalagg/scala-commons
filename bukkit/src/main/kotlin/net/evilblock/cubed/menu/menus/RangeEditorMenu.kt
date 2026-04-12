

package net.evilblock.cubed.menu.menus

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

abstract class RangeEditorMenu(
    private var min: Int,
    private var max: Int
) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (i in 0..8) {
            buttons[i] = Button.placeholder(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()!!, 15, " ")
        }

        buttons[2] = EditMinButton()
        buttons[6] = EditMaxButton()

        return buttons
    }

    abstract fun onChange(min: Int, max: Int)

    protected open inner class EditMinButton : TexturedHeadButton(STONE_ARROW_DOWN_HEAD_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Min ${ChatColor.GRAY}(${min})"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to increase min by +1")
            description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to decrease min by -1")
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}SHIFT LEFT-CLICK ${ChatColor.GREEN}to increase min by +10")
            description.add("${ChatColor.RED}${ChatColor.BOLD}SHIFT RIGHT-CLICK ${ChatColor.RED}to decrease min by -10")
            return description
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            try {
                val mod = if (clickType.isShiftClick) 10 else 1
                when {
                    clickType.isLeftClick -> {
                        onChange(min + mod, max)
                        min += mod
                    }
                    clickType.isRightClick -> {
                        onChange(min - mod, max)
                        min -= mod
                    }
                }
            } catch (e: IllegalStateException) {
                player.sendMessage("${ChatColor.RED}${e.message}!")
            }
        }
    }

    protected open inner class EditMaxButton : TexturedHeadButton(STONE_ARROW_UP_HEAD_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Max ${ChatColor.GRAY}(${max})"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to increase max by +1")
            description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to decrease max by -1")
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}SHIFT LEFT-CLICK ${ChatColor.GREEN}to increase max by +10")
            description.add("${ChatColor.RED}${ChatColor.BOLD}SHIFT RIGHT-CLICK ${ChatColor.RED}to decrease max by -10")
            return description
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            try {
                val mod = if (clickType.isShiftClick) 10 else 1
                when {
                    clickType.isLeftClick -> {
                        onChange(min, max + mod)
                        max += mod
                    }
                    clickType.isRightClick -> {
                        onChange(min, max - mod)
                        max -= mod
                    }
                }
            } catch (e: IllegalStateException) {
                player.sendMessage("${ChatColor.RED}${e.message}!")
            }
        }
    }

    companion object {
        private const val STONE_ARROW_UP_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNThmZTI1MWE0MGU0MTY3ZDM1ZDA4MWMyNzg2OWFjMTUxYWY5NmI2YmQxNmRkMjgzNGQ1ZGM3MjM1ZjQ3NzkxZCJ9fX0="
        private const val STONE_ARROW_DOWN_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWI3Y2U2ODNkMDg2OGFhNDM3OGFlYjYwY2FhNWVhODA1OTZiY2ZmZGFiNmI1YWYyZDEyNTk1ODM3YTg0ODUzIn19fQ=="
    }

}
