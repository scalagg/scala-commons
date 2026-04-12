

package net.evilblock.cubed.menu.buttons

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.ServerVersion
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class TexturedHeadButton(private val texture: String? = null) : Button() {

    open fun getTexture(player: Player): String {
        return texture!!
    }

    override fun getMaterial(player: Player): XMaterial
    {
        return XMaterial.PLAYER_HEAD
    }

    override fun getDamageValue(player: Player): Byte {
        return if (ServerVersion.getVersion().isNewerThanOrEquals(ServerVersion.v1_13)) 0.toByte() else 3.toByte()
    }

    override fun getButtonItem(player: Player): ItemStack {
        return ItemUtils.applySkullTexture(super.getButtonItem(player), getTexture(player))
    }

}
