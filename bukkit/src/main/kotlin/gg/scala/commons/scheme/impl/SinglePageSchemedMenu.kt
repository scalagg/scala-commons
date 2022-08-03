package gg.scala.commons.scheme.impl

import gg.scala.commons.scheme.AbstractCompositeSchemedMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/1/2022
 */
class SinglePageSchemedMenu : AbstractCompositeSchemedMenu<Menu>()
{
    override fun createMenu(player: Player): Menu
    {
        return object : Menu(
            this@SinglePageSchemedMenu.title
        )
        {
            init
            {
                this@SinglePageSchemedMenu.configuration.invoke(this)
            }

            override fun getButtons(player: Player): Map<Int, Button>
            {
                return this@SinglePageSchemedMenu.buttons
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
                        this@SinglePageSchemedMenu.onClose.invoke(this, player)
                    }
                }
            }

            override fun size(buttons: Map<Int, Button>) =
                this@SinglePageSchemedMenu.pattern.size * 9
        }
    }
}
