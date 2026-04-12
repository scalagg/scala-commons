

package net.evilblock.cubed.entity.hologram.menu

import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.cubed.entity.hologram.updating.FormatUpdatingHologramEntity
import net.evilblock.cubed.entity.menu.EditEntityMenu
import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.text.TextUtil
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class EditHologramTextMenu(private val hologram: HologramEntity) : TextEditorMenu(getSourceLines(hologram)) {

    init {
        supportsColors = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit Holograms Text"
    }

    override fun onClose(player: Player) {
        Tasks.delayed(1L) {
            EditEntityMenu(hologram).openMenu(player)
        }
    }

    override fun onSave(player: Player, list: List<String>) {
        val lines = list.map { line ->
            if (TextUtil.isBlank(text = line)) {
                ""
            } else {
                line
            }
        }

        if (hologram is FormatUpdatingHologramEntity) {
            hologram.format = lines.toMutableList()
        } else {
            hologram.updateLines(lines)
        }

        Tasks.async {
            EntityHandler.close()
        }
    }

    override fun getPromptBuilder(player: Player, index: Int): EzPrompt.Builder {
        return EzPrompt.Builder()
            .promptText("${ChatColor.GREEN}Please enter the new text.")
            .charLimit(100)
    }

    companion object {
        private fun getSourceLines(hologram: HologramEntity): List<String> {
            return if (hologram is FormatUpdatingHologramEntity) {
                hologram.format
            } else {
                hologram.getLines().toMutableList()
            }
        }
    }

}
