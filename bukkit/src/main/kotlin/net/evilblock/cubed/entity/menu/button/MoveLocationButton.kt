package net.evilblock.cubed.entity.menu.button

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.tool.EntityMoveTool
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.text.TextSplitter
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue

class MoveLocationButton(private val entity: Entity) : Button() {

    override fun getName(player: Player): String {
        return "${CC.GREEN}Move Location"
    }

    override fun getDescription(player: Player): List<String> {
        return arrayListOf<String>().also { desc ->
            desc.addAll(TextSplitter.split(text = "Move this NPC's location."))
            desc.add("")
            desc.add("${CC.GREEN}Left-Click to move location.")
        }
    }

    override fun getMaterial(player: Player): XMaterial
    {
        return XMaterial.PIG_SPAWN_EGG
    }

    override fun getButtonItem(player: Player): ItemStack {
        return ItemUtils.setMonsterEggType(super.getButtonItem(player), EntityType.VILLAGER)
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        entity.updateVisibility(hidden = true)

        player.closeInventory()
        player.inventory.addItem(EntityMoveTool.makeMoveTool(entity))

        player.setMetadata(
            "moving",
            FixedMetadataValue(ScalaCommonsSpigot.instance, entity.uuid.toString())
        )

        player.updateInventory()
        player.sendMessage("${CC.GREEN}Place the mob egg wherever you'd like to move the NPC.")
    }

}
