

package net.evilblock.cubed.menu.buttons

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.menu.Button
import org.bukkit.Material
import org.bukkit.entity.Player

class GlassButton(private val data: Byte) : Button() {

    override fun getName(player: Player): String {
        return " "
    }

    override fun getDescription(player: Player): List<String> {
        return emptyList()
    }

    override fun getMaterial(player: Player): XMaterial {
        return XMaterial.BLACK_STAINED_GLASS_PANE
    }

    override fun getDamageValue(player: Player): Byte {
        return data
    }

}
