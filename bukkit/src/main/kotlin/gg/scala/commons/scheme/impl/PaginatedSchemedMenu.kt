package gg.scala.commons.scheme.impl

import gg.scala.commons.scheme.AbstractCompositeSchemedMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.entity.Player

class PaginatedSchemedMenu : AbstractCompositeSchemedMenu<PaginatedMenu>()
{
    override fun createMenu(player: Player): PaginatedMenu
    {
        return object : PaginatedMenu()
        {
            init
            {
                this@PaginatedSchemedMenu.configuration.invoke(this)
            }

            override fun getPrePaginatedTitle(player: Player) =
                this@PaginatedSchemedMenu.title

            override fun getAllPagesButtons(player: Player): Map<Int, Button>
            {
                return this@PaginatedSchemedMenu.buttons
                    .mapValues {
                        it.value.invoke(player, it.key)
                    }
            }

            override fun onClose(player: Player, manualClose: Boolean)
            {
                if (manualClose)
                {
                    Tasks.delayed(1L)
                    {
                        this@PaginatedSchemedMenu.onClose.invoke(this, player)
                    }
                }
            }

            override fun size(buttons: Map<Int, Button>) =
                this@PaginatedSchemedMenu.pattern.size * 9
        }
    }
}
