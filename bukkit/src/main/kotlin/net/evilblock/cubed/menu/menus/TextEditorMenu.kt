/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.menus

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

abstract class TextEditorMenu(protected val lines: LinkedList<String>) : PaginatedMenu() {

    constructor(lines: Collection<String>) : this(lines = LinkedList(lines))

    private var changesMade: Boolean = false
    protected var supportsColors: Boolean = false

    init {
        updateAfterClick = true
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[2] = AddLineButton()

            if (changesMade) {
                buttons[6] = SaveButton()
            } else {
                buttons[6] = NoChangesButton()
            }

//            for (i in 1 until 8) {
//                if (!buttons.containsKey(i)) {
//                    buttons[i] = GlassButton(0)
//                }
//            }
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in lines.indices) {
                buttons[buttons.size] = LineButton(i)
            }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 45
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

    abstract fun onSave(player: Player, list: List<String>)

    abstract fun onClose(player: Player)

    open fun getPromptBuilder(player: Player, index: Int): EzPrompt.Builder {
        return EzPrompt.Builder()
            .charLimit(100).also {
                if (index == -1) {
                    it.promptText("${CC.GREEN}Please enter the new text.")
                } else {
                    it.promptText("${CC.GREEN}Please enter the new text for line $index.")
                }
            }
    }

    private inner class AddLineButton : AddButton() {
        override fun getName(player: Player): String {
            return "${CC.GRAY}» ${CC.PRI}Create New Line"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "${CC.GRAY}Create a new line by completing",
                "${CC.GRAY}the setup procedure.",
                "",
                "${CC.GREEN}Left-Click to create a new line."
            )
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            getPromptBuilder(player, -1)
                .acceptInput { input ->
                    if (supportsColors) {
                        lines.add(Color.translate(input))
                    } else {
                        lines.add(input)
                    }

                    changesMade = true

                    openMenu(player)
                }
                .build()
                .start(player)
        }
    }

    private inner class LineButton(internal val index: Int) : Button() {
        override fun getName(player: Player): String {
            val text = lines[index]

            if (text.isEmpty() || text.isBlank()) {
                return "${CC.RED}${CC.BOLD}Blank Line"
            }

            return CC.WHITE + text
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("${CC.GRAY}Line ${index + 1}")
            description.add("")
            description.add("${CC.GREEN}Left-Click to edit line.")
            description.add("${CC.RED}Right-Click to delete line.")
            description.add("")

            val leftArrowColor = if (index > 0) {
                CC.BLUE
            } else {
                CC.GRAY
            }

            val rightArrowColor = if (index < lines.lastIndex) {
                CC.BLUE
            } else {
                CC.GRAY
            }

            description.add("$leftArrowColor${CC.BOLD}⬅ ${CC.YELLOW}Shift Left/Right Click $rightArrowColor${CC.BOLD}➡")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.PAPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isShiftClick) {
                if (!(clickType.isLeftClick || clickType.isRightClick)) {
                    return
                }

                val canShiftLeft = index > 0
                val canShiftRight = index < lines.lastIndex

                val indexMod = if (canShiftLeft && clickType.isLeftClick) {
                    -1
                } else if (canShiftRight && clickType.isRightClick) {
                    1
                } else {
                    0
                }

                if (indexMod != 0) {
                    val line = lines.removeAt(index)
                    lines.add(index + indexMod, line)

                    changesMade = true
                }
            } else {
                if (clickType.isLeftClick) {
                    getPromptBuilder(player, index)
                        .acceptInput { input ->
                            lines.removeAt(index)

                            if (supportsColors) {
                                lines.add(index, Color.translate(input))
                            } else {
                                lines.add(index, input)
                            }

                            changesMade = true

                            openMenu(player)
                        }
                        .build()
                        .start(player)
                } else if (clickType.isRightClick) {
                    ConfirmMenu("Are you sure?") { confirmed ->
                        if (confirmed) {
                            lines.removeAt(index)

                            changesMade = true
                        } else {
                            player.sendMessage("${CC.YELLOW}No changes made.")
                        }

                        openMenu(player)
                    }.openMenu(player)
                }
            }
        }
    }

    private inner class SaveButton : Button() {
        override fun getName(player: Player): String {
            return "${CC.GRAY}» ${CC.PRI}Save Changes"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "${CC.GRAY}Saves any changes made to the",
                "${CC.GRAY}lines of text.",
                "",
                "${CC.GREEN}Left-Click to save changes."
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.WOOL
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
            return "${CC.GRAY}» ${CC.RED}No Changes Made"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "${CC.GRAY}No changes have been made to the",
                "${CC.GRAY}lines of text."
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.WOOL
        }

        override fun getDamageValue(player: Player): Byte {
            return 8
        }
    }

}