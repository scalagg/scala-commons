

package net.evilblock.cubed.entity.menu

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import org.bukkit.entity.Player

class EditEntityMenu(private val entity: Entity) : PaginatedMenu() {

    init
    {
        placeholdBorders = true
        updateAfterClick = false
    }

    override fun size(buttons: Map<Int, Button>) = 36

    override fun getAllPagesButtonSlots() = listOf(
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25
    )

    override fun getPrePaginatedTitle(player: Player) =
        "Edit ${entity.getTypeName()} #${entity.id - Entity.initialId}"

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = mutableMapOf<Int, Button>()

        entity.getEditorButtons(player, this)
            .forEach {
                buttons[buttons.size] = it
            }

        return buttons
    }

}
