package gg.scala.commons.scoping

import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player

class EditScopesMenu<T : ServerScoped>(
    private val scoped: T,
    private val onClose: (Player) -> Unit,
    private val onSave: (Player, T) -> Unit,
) : TextEditorMenu(scoped.scopes.toList())
{
    override fun getPrePaginatedTitle(player: Player) = "Editing scopes..."

    override fun onClose(player: Player)
    {
        onClose(player)
    }

    override fun onSave(player: Player, list: List<String>)
    {
        if (list.isEmpty())
        {
            player.sendMessage("${CC.RED}No scopes were given, so the global scope was added back by default.")
            scoped.scopes = setOf("global")
            onSave(player, scoped)
            return
        }

        scoped.scopes = list.toSet()
        onSave(player, scoped)
    }
}
