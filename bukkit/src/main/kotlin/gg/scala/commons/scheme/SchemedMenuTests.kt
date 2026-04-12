package gg.scala.commons.scheme

import gg.scala.commons.scheme.impl.SinglePageSchemedMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Rainbow
import org.bukkit.Material
import org.bukkit.entity.Player

object SchemedMenuTests
{
    fun test(player: Player)
    {
        val singlePage = SinglePageSchemedMenu()
            .configuration {
                this.autoUpdate = true
                this.autoUpdateInterval = 10L
            }
            .pattern(
                "0Z0Z0Z0Z0",
                "ZXXXXXXXZ",
                "0XXXXXXX0",
                "Z0Z0Z0Z0Z",
            )
            .map('0') { _, _ ->
                ItemBuilder
                    .copyOf(
                        PaginatedMenu.PLACEHOLDER
                            .getButtonItem(player)
                    )
                    .data(2)
                    .toButton()
            }
            .map('Z') { _, _ ->
                ItemBuilder
                    .copyOf(
                        PaginatedMenu.PLACEHOLDER
                            .getButtonItem(player)
                    )
                    .data(1)
                    .toButton()
            }
            .map('X') { _, position ->
                ItemBuilder
                    .of(Material.PAPER)
                    .name("${Rainbow.currentColor}Example $position")
                    .toButton()
            }
            .compose()

        singlePage.createAndOpen(player)
    }
}
