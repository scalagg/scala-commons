/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.menus

import com.cryptomorin.xseries.XMaterial
import gg.scala.commons.util.prompts.collectResponses
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Color
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import java.util.*

abstract class MapEditorMenu(protected val lines: MutableMap<String, String>) : PaginatedMenu() {

    companion object {
        @JvmStatic
        val SLOTS = listOf(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
        )
    }

    private var changesMade: Boolean = false
    protected var supportsColors: Boolean = false

    init {
        updateAfterClick = true
        placeholdBorders = true
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[2] = AddLineButton()

            if (changesMade) {
                buttons[6] = SaveButton()
            } else {
                buttons[6] = NoChangesButton()
            }
        }
    }

    override fun getAllPagesButtonSlots(): List<Int> = SLOTS

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in lines.keys) {
                buttons[buttons.size] = LineButton(i)
            }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 45
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 27
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            if (changesMade) {
                Tasks.delayed(1L) {
                    ConfirmMenu("Discard changes?") { confirmed ->
                        if (confirmed) {
                            player.sendMessage("${CC.YELLOW}Discarded changes.")

                            onClose(player)
                        } else {
                            Tasks.delayed(1L) {
                                openMenu(player)
                            }

                            return@ConfirmMenu
                        }
                    }.openMenu(player)
                }
            } else {
                onClose(player)
            }
        }
    }

    abstract fun onSave(player: Player, list: Map<String, String>)

    abstract fun onClose(player: Player)

    open fun getPromptBuilder(player: Player) = player.collectResponses {
        with("key", "Key (The identifier)")
        with("Value", "Value (The content, may be colored)")
    }

    private inner class AddLineButton : AddButton() {
        override fun getName(player: Player): String {
            return "${CC.GREEN}Create a new pair"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "${CC.GRAY}Add a new pair to this map",
                "${CC.GRAY}by following our procedure.",
                "",
                "${CC.YELLOW}Left-Click to add new pair!",
            )
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            getPromptBuilder(player)
                .subscribe({ input ->
                    if (supportsColors) {
                        lines[input["key"]!!] = Color.translate(input["value"]!!)
                    } else {
                        lines[input["key"]!!] = input["value"]!!
                    }

                    changesMade = true

                    openMenu(player)
                }, {
                    player.sendMessage("${CC.RED}No changes made.")
                    openMenu(player)
                })
        }
    }

    private inner class LineButton(internal val key: String) : Button() {
        override fun getName(player: Player): String {
            return CC.WHITE + key
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("${CC.WHITE}Value: ${CC.GREEN}${lines[key] ?: "????"}")
            description.add("")
            description.add("${CC.GREEN}Left-Click to edit pair.")
            description.add("${CC.RED}Right-Click to delete pair.")
            description.add("")

            return description
        }

        override fun getMaterial(player: Player): XMaterial {
            return XMaterial.PAPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                getPromptBuilder(player)
                    .subscribe({ input ->
                        if (supportsColors) {
                            lines[input["key"]!!] = Color.translate(input["value"]!!)
                        } else {
                            lines[input["key"]!!] = input["value"]!!
                        }

                        changesMade = true

                        openMenu(player)
                    }, {
                        player.sendMessage("${CC.RED}No changes made.")
                        openMenu(player)
                    })
            } else if (clickType.isRightClick) {
                ConfirmMenu("Are you sure?") { confirmed ->
                    if (confirmed) {
                        lines.remove(key)

                        changesMade = true
                    } else {
                        player.sendMessage("${CC.YELLOW}No changes made.")
                    }

                    openMenu(player)
                }.openMenu(player)
            }
        }
    }

    private inner class SaveButton : Button() {
        override fun getName(player: Player): String {
            return "${CC.GREEN}Save changes"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "${CC.GRAY}Saves any changes made",
                "${CC.GRAY}to these lines of text.",
                "",
                "${CC.YELLOW}Click to save changes!"
            )
        }

        override fun getMaterial(player: Player): XMaterial {
            return XMaterial.LIME_WOOL
        }

        override fun getDamageValue(player: Player): Byte {
            return 13
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            onSave(player, lines)
            changesMade = false
        }
    }

    private inner class NoChangesButton : Button() {
        override fun getName(player: Player): String {
            return "${CC.GREEN}Save changes"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "${CC.GRAY}Saves any changes made",
                "${CC.GRAY}to these lines of text.",
                "",
                "${CC.RED}You need to make changes!"
            )
        }

        override fun getMaterial(player: Player): XMaterial {
            return XMaterial.LIGHT_GRAY_WOOL
        }

        override fun getDamageValue(player: Player): Byte {
            return 8
        }
    }

}