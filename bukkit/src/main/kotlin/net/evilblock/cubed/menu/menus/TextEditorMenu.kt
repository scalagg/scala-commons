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
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import org.bukkit.ChatColor
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

            for (i in 1 until 8) {
                if (!buttons.containsKey(i)) {
                    buttons[i] = GlassButton(0)
                }
            }
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
                            player.sendMessage("${ChatColor.YELLOW}Discarded changes.")

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
                    it.promptText("${ChatColor.GREEN}Please enter the new text.")
                } else {
                    it.promptText("${ChatColor.GREEN}Please enter the new text for line $index.")
                }
            }
    }

    private inner class AddLineButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Create New Line"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Create a new line by completing",
                "${ChatColor.GRAY}the setup procedure.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to create new line"
            )
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            getPromptBuilder(player, -1)
                .acceptInput { input ->
                    if (supportsColors) {
                        lines.add(ChatColor.translateAlternateColorCodes('&', input))
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
                return "${ChatColor.RED}${ChatColor.BOLD}BLANK LINE"
            }

            return text
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("${ChatColor.GRAY}Line ${index + 1}")
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit line")
            description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to delete line")
            description.add("")

            val leftArrowColor = if (index > 0) {
                ChatColor.BLUE
            } else {
                ChatColor.GRAY
            }

            val rightArrowColor = if (index < lines.lastIndex) {
                ChatColor.BLUE
            } else {
                ChatColor.GRAY
            }

            description.add("$leftArrowColor${ChatColor.BOLD}⬅ ${ChatColor.YELLOW}${ChatColor.BOLD}SHIFT LEFT/RIGHT CLICK $rightArrowColor${ChatColor.BOLD}➡")

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
                                lines.add(index, ChatColor.translateAlternateColorCodes('&', input))
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
                            player.sendMessage("${ChatColor.YELLOW}No changes made.")
                        }

                        openMenu(player)
                    }.openMenu(player)
                }
            }
        }
    }

    private inner class SaveButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}Save Changes"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Saves any changes made to the",
                "${ChatColor.GRAY}lines of text.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to save changes"
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
            return "${ChatColor.GRAY}${ChatColor.BOLD}No Changes Made"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}No changes have been made to the",
                "${ChatColor.GRAY}lines of text."
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