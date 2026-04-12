

package net.evilblock.cubed.menu.buttons

import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CountdownButton(val text: String, val number: Int) : TexturedHeadButton(texture = "") {

    override fun getName(player: Player): String {
        return text
    }

    override fun getButtonItem(player: Player): ItemStack {
        val texture = when (number) {
            1 -> OAK_WOOD_1
            2 -> OAK_WOOD_2
            3 -> OAK_WOOD_3
            else -> OAK_WOOD_DOT
        }

        return ItemUtils.applySkullTexture(super.getButtonItem(player), texture)
    }

    companion object {
        private val OAK_WOOD_1 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzFiYzJiY2ZiMmJkMzc1OWU2YjFlODZmYzdhNzk1ODVlMTEyN2RkMzU3ZmMyMDI4OTNmOWRlMjQxYmM5ZTUzMCJ9fX0="
        private val OAK_WOOD_2 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNkOWVlZWU4ODM0Njg4ODFkODM4NDhhNDZiZjMwMTI0ODVjMjNmNzU3NTNiOGZiZTg0ODczNDE0MTk4NDcifX19"
        private val OAK_WOOD_3 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQ0ZWFlMTM5MzM4NjBhNmRmNWU4ZTk1NTY5M2I5NWE4YzNiMTVjMzZiOGI1ODc1MzJhYzA5OTZiYzM3ZTUifX19"
        private val OAK_WOOD_DOT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQ1NjQ4NWFhMjM2MTQzNGNiM2JlNTMxYmMxMjE2NTlmNTU1YTRhYWYyNjFkMjFhMDZkN2FmZGU1YWE2MGIyIn19fQ=="
    }

}