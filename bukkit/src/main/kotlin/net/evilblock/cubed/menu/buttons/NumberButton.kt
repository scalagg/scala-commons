/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.buttons

import net.evilblock.cubed.util.CC
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

abstract class NumberButton(
    protected var number: Int,
    protected var range: IntRange? = null,
    headTexture: String = NUMBER_HEAD_TEXTURE
) : TexturedHeadButton(headTexture) {

    override fun getDescription(player: Player): List<String> {
        return arrayListOf<String>().also { desc ->
            desc.add("")
            desc.add("${CC.GREEN}Left-click${ChatColor.GRAY}: increase by +1")
            desc.add("${CC.RED}Right-click${ChatColor.GRAY}: decrease by -1")
            desc.add("")
            desc.add("${CC.B_GREEN}Shift & Left-click${ChatColor.GRAY}: increase by +10")
            desc.add("${CC.B_RED}Shift & Right-click${ChatColor.GRAY}: decrease by -10")
        }
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        try {
            val mod = if (clickType.isShiftClick) {
                10
            } else {
                1
            }

            when {
                clickType.isLeftClick -> {
                    val result = number + mod

                    if (range != null && result !in range!!) {
                        player.sendMessage("${ChatColor.RED}Value must be within range ${range!!.first} - ${range!!.last}!")
                        return
                    }

                    onChange(result)
                    number = result
                }
                clickType.isRightClick -> {
                    val result = number - mod

                    if (range != null && result !in range!!) {
                        player.sendMessage("${ChatColor.RED}Value must be within range ${range!!.first} - ${range!!.last}!")
                        return
                    }

                    onChange(result)
                    number = result
                }
            }
        } catch (e: IllegalStateException) {
            player.sendMessage("${ChatColor.RED}${e.message}!")
        }
    }

    abstract fun onChange(number: Int)

    companion object {
        private const val NUMBER_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzFhOTQ2M2ZkM2M0MzNkNWUxZDlmZWM2ZDVkNGIwOWE4M2E5NzBiMGI3NGRkNTQ2Y2U2N2E3MzM0OGNhYWIifX19"
    }

}
