/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.menus

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import org.bukkit.entity.Player
import net.evilblock.cubed.menu.buttons.BooleanButton
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import org.bukkit.ChatColor
import org.bukkit.Material
import java.util.HashMap

open class ConfirmMenu(
    private val title: String = "Are you sure?",
    private val extraInfo: List<String> = emptyList(),
    private val confirm: Boolean = true,
    private val callback: (Boolean) -> Unit
) : Menu() {

    var called: Boolean = false

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = HashMap<Int, Button>()

        if (extraInfo.isEmpty()) {
            buttons[3] = BooleanButton(this, true, confirm)
            buttons[5] = BooleanButton(this, false, confirm)
        } else {
            buttons[2] = BooleanButton(this, true, confirm)
            buttons[6] = BooleanButton(this, false, confirm)
            buttons[4] = ExtraInfoButton()
        }

        for (i in 0..8) {
            if (!buttons.containsKey(i)) {
                buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 14.toByte())
            }
        }

        return buttons
    }

    override fun getTitle(player: Player): String {
        return title
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        Cubed.instance.server.scheduler.runTaskLater(Cubed.instance, {
            if (!called) {
                invokeCallback(false)
            }
        }, 1L)
    }

    fun invokeCallback(confirm: Boolean) {
        called = true
        callback.invoke(confirm)
    }

    private inner class ExtraInfoButton : TexturedHeadButton(WOOD_EXCLAMATION_POINT) {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}$title"
        }

        override fun getDescription(player: Player): List<String> {
            return extraInfo
        }
    }

    companion object {
        private const val WOOD_EXCLAMATION_POINT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmUzZjUwYmE2MmNiZGEzZWNmNTQ3OWI2MmZlZGViZDYxZDc2NTg5NzcxY2MxOTI4NmJmMjc0NWNkNzFlNDdjNiJ9fX0="
    }

}