/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.pagination

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.distribution.MenuRowDistribution
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player
import kotlin.math.ceil

abstract class PaginatedMenu : Menu()
{
    companion object
    {
        @JvmStatic
        val PLACEHOLDER = ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE)
            .data(15.toShort())
            .name(" ")
            .toButton()
    }

    var page = 1

    var verticalView = false
    var applyHeader = true

    override fun getTitleContextual(player: Player, items: Int): String
    {
        val pages = if (items != -1)
        {
            getPagesGivenAmount(
                player, buttonAmount = items
            )
        } else
        {
            getPages(player)
        }

        return "${getPrePaginatedTitle(player)}${
            if (pages > 1) "  ($page/$pages)" else ""
        }"
    }

    fun modPage(player: Player, mod: Int)
    {
        page += mod
        buttons.clear()
        openMenu(player)
    }

    private fun getPagesGivenAmount(player: Player, buttonAmount: Int): Int
    {
        return if (buttonAmount == 0) 1 else
            ceil(buttonAmount / getMaxItemsPerPage(player).toDouble()).toInt()
    }

    internal fun getPages(player: Player) =
        getPagesGivenAmount(
            player,
            buttonAmount = getAllPagesButtons(player).size
        )

    override fun getButtons(player: Player): MutableMap<Int, Button>
    {
        val buttons = HashMap<Int, Button>()

        if (applyHeader)
        {
            for (i in 1..7)
            {
                buttons[i] = PLACEHOLDER
            }
        }

        val pageButtonSlots = getPageButtonSlots()

        if (pageButtonSlots != null)
        {
            val pages = getPages(player)
            buttons[pageButtonSlots.first] = PageButton(-1, menu = this, pages)
            buttons[pageButtonSlots.second] = PageButton(1, menu = this, pages)
        }

        val buttonSlots = getAllPagesButtonSlots()
            .ifEmpty {
                this.distribution.allDistributed(buttons)
            }

        if (buttonSlots.isEmpty())
        {
            val minIndex = ((page - 1) * getMaxItemsPerPage(player).toDouble()).toInt()
            val maxIndex = (page * getMaxItemsPerPage(player).toDouble()).toInt()

            val allPageButtons = getAllPagesButtons(player)
            for (entry in allPageButtons.entries)
            {
                var ind = entry.key
                if (ind in minIndex until maxIndex)
                {
                    ind -= (getMaxItemsPerPage(player) * (page - 1).toDouble()).toInt() - 9
                    buttons[getButtonsStartOffset() + ind] = entry.value
                }
            }
        } else
        {
            val maxPerPage = buttonSlots.size

            val minIndex = (page - 1) * maxPerPage
            val maxIndex = page * maxPerPage

            val allPageButtons = getAllPagesButtons(player)
            for ((index, entry) in allPageButtons.entries.withIndex())
            {
                if (index in minIndex until maxIndex)
                {
                    buttons[buttonSlots[index - minIndex]] = entry.value
                }
            }
        }

        // insert global buttons AFTER inserting entry buttons
        val global = getGlobalButtons(player)
        if (global != null)
        {
            for ((key, value) in global)
            {
                buttons[key] = value
            }
        }

        return buttons
    }

    open fun getMaxItemsPerPage(player: Player): Int
    {
        return 18
    }

    open fun getButtonsStartOffset(): Int
    {
        return 0
    }

    open fun getPageButtonSlots(): Pair<Int, Int>?
    {
        return Pair(0, 8)
    }

    open fun getGlobalButtons(player: Player): Map<Int, Button>?
    {
        return null
    }

    abstract fun getPrePaginatedTitle(player: Player): String

    abstract fun getAllPagesButtons(player: Player): Map<Int, Button>

    open fun getAllPagesButtonSlots(): List<Int>
    {
        return emptyList()
    }
}
