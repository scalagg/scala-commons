package gg.scala.commons.sign

import com.comphenix.packetwrapper.WrapperPlayClientUpdateSign
import com.comphenix.packetwrapper.WrapperPlayServerBlockChange
import com.comphenix.packetwrapper.WrapperPlayServerOpenSignEditor
import com.comphenix.protocol.wrappers.BlockPosition
import com.comphenix.protocol.wrappers.WrappedBlockData
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryType
import java.util.*

/**
 * @author GrowlyX
 * @since 8/22/2022
 */
class Sign(
    val uniqueId: UUID,
    val lines: List<String>,
    val action: (List<String>) -> Unit
)
{
    fun close()
    {
        val bukkitPlayer = Bukkit.getPlayer(uniqueId)
            ?: return

        val inventoryType = bukkitPlayer
            .openInventory.topInventory.type

        if (inventoryType == InventoryType.CRAFTING)
        {
            bukkitPlayer.openInventory.close()
        }
    }

    fun open()
    {
        val bukkitPlayer = Bukkit.getPlayer(uniqueId)
            ?: return

        val blockPosition = BlockPosition(
            bukkitPlayer.location.blockX, 1,
            bukkitPlayer.location.blockZ
        )

        val packet = WrapperPlayServerBlockChange()
        packet.location = blockPosition
        packet.blockData = WrappedBlockData
            .createData(Material.WALL_SIGN)

        val tileEntity = WrapperPlayClientUpdateSign()
        tileEntity.location = blockPosition
        tileEntity.lines = lines.toTypedArray()

        val editor = WrapperPlayServerOpenSignEditor()
        editor.location = blockPosition

        listOf(packet, tileEntity, editor)
            .forEach {
                it.sendPacket(bukkitPlayer)
            }
    }
}
