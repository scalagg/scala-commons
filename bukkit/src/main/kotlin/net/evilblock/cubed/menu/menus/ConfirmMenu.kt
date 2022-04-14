/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.menus

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.BooleanButton
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

open class ConfirmMenu(
    title: String = "Are you sure?",
    private val extraInfo: List<String> = listOf("${CC.GRAY}Do you really want to do this?"),
    private val confirm: Boolean = true,
    private val callback: (Boolean) -> Unit
) : Menu(title)
{
    var called: Boolean = false

    init
    {
        placeholder = true
    }

    override fun getButtons(player: Player): Map<Int, Button>
    {
        val buttons = HashMap<Int, Button>()

        if (extraInfo.isEmpty())
        {
            buttons[3] = BooleanButton(this, true, confirm)
            buttons[5] = BooleanButton(this, false, confirm)
        } else
        {
            buttons[2] = BooleanButton(this, true, confirm)
            buttons[6] = BooleanButton(this, false, confirm)
            buttons[4] = ExtraInfoButton()
        }

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean)
    {
        Cubed.instance.server.scheduler.runTaskLater(Cubed.instance, {
            if (!called)
            {
                invokeCallback(false)
            }
        }, 1L)
    }

    fun invokeCallback(confirm: Boolean)
    {
        called = true
        callback.invoke(confirm)
    }

    private inner class ExtraInfoButton : Button()
    {
        override fun getName(player: Player): String
        {
            return "${ChatColor.RED}Are you sure?"
        }

        override fun getDescription(player: Player): List<String>
        {
            return extraInfo
        }

        override fun getMaterial(player: Player): XMaterial
        {
            return XMaterial.OAK_WALL_SIGN
        }
    }
}
