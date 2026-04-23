package gg.scala.commons.sign

import net.evilblock.cubed.ScalaCommonsSpigot
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import java.util.UUID

class Sign(
    val uniqueId: UUID,
    val lines: Array<String>,
    val action: (Array<String>) -> Unit
)
{
    fun close()
    {
        val bukkitPlayer = Bukkit.getPlayer(uniqueId) ?: return
        if (bukkitPlayer.openInventory.topInventory.type == InventoryType.CRAFTING)
        {
            bukkitPlayer.openInventory.close()
        }
    }

    fun open()
    {
        val bukkitPlayer = Bukkit.getPlayer(uniqueId) ?: return
        SignGUI(ScalaCommonsSpigot.instance).open(bukkitPlayer, lines) { _, result ->
            action(result)
            close()
        }
    }
}
