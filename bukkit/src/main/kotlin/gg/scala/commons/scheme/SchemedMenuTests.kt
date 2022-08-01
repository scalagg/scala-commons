package gg.scala.commons.scheme

import gg.scala.commons.scheme.impl.SinglePageSchemedMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/1/2022
 */
object SchemedMenuTests
{
    fun test(player: Player)
    {
        val singlePage = SinglePageSchemedMenu()
            .configuration {
                this.autoUpdate = true
            }
            .pattern(
                "000000000",
                "0XXXXXXX0",
                "0XXXXXXX0",
                "000000000",
            )
            .map('0', PaginatedMenu.PLACEHOLDER)
            .map(
                'X',
                ItemBuilder
                    .of(Material.PAPER)
                    .name("${CC.GREEN}Example")
                    .toButton()
            )
            .compose()

        singlePage.createAndOpen(player)
    }
}
