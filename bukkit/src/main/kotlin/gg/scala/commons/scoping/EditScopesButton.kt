package gg.scala.commons.scoping

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 2/9/2025
 */
fun <T : ServerScoped> Player.editScopesButton(
    scoped: T,
    onClose: (Player) -> Unit,
    onSave: (Player, T) -> Unit
) = ItemBuilder
    .of(XMaterial.BOOK)
    .name("${CC.YELLOW}Scopes:")
    .setLore(scoped.scopes.toList().map { "${CC.WHITE}- $it" })
    .toButton { _, _ ->
        EditScopesMenu(scoped, onClose, onSave).openMenu(player)
    }
