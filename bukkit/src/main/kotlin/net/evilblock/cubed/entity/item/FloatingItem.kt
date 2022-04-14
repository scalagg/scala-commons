package net.evilblock.cubed.entity.item

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.menus.SelectItemStackMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.AngleUtils
import net.evilblock.cubed.util.nms.DataWatcherUtil
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.cubed.util.nms.MinecraftReflection
import net.evilblock.cubed.util.text.TextSplitter
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class FloatingItem(var itemStack: ItemStack, location: Location) : Entity(location) {

    @Transient private val armorStandId: Int = nextEntityId()

    init {
        persistent = false
    }

    override fun getTypeName(): String {
        return "FloatingItem"
    }

    override fun initializeData() {
        super.initializeData()

        persistent = false
    }

    override fun getEditorButtons(player: Player, menu: Menu): MutableList<Button> {
        return super.getEditorButtons(player, menu).also { list ->
            list.add(EditFloatingItemButton())
        }
    }

    override fun sendSpawnPackets(player: Player) {
        val itemPacket = MinecraftProtocol.newPacket("PacketPlayOutSpawnEntity")
        Reflection.setDeclaredFieldValue(itemPacket, "a", id)
        Reflection.setDeclaredFieldValue(itemPacket, "b", MinecraftReflection.mathFloor(location.x * 32.0))
        Reflection.setDeclaredFieldValue(itemPacket, "c", MinecraftReflection.mathFloor(location.y * 32.0))
        Reflection.setDeclaredFieldValue(itemPacket, "d", MinecraftReflection.mathFloor(location.z * 32.0))
        Reflection.setDeclaredFieldValue(itemPacket, "h", 0.toByte())
        Reflection.setDeclaredFieldValue(itemPacket, "i", 0.toByte())

        val itemDataWatcher = DataWatcherUtil.new()
        DataWatcherUtil.register(itemDataWatcher, 0, 0.toByte()) // flag
        DataWatcherUtil.register(itemDataWatcher, 1, 0.toShort()) // air ticks
        DataWatcherUtil.register(itemDataWatcher, 2, "") // custom name
        DataWatcherUtil.register(itemDataWatcher, 3, 0.toByte()) // custom name visible
        DataWatcherUtil.register(itemDataWatcher, 4, 0.toByte()) // silent
        DataWatcherUtil.setFlag(itemDataWatcher, 5, false)

        val itemMetadataPacket = MinecraftProtocol.newPacket("PacketPlayOutEntityMetadata")
        Reflection.setDeclaredFieldValue(itemMetadataPacket, "a", id)
        Reflection.setDeclaredFieldValue(itemMetadataPacket, "b", DataWatcherUtil.getWatchedObjects(itemDataWatcher) as Any)

        val armorStandDataWatcher = DataWatcherUtil.new()
        DataWatcherUtil.register(armorStandDataWatcher, 0, 0.toByte()) // flag
        DataWatcherUtil.register(armorStandDataWatcher, 1, 0.toShort()) // air ticks
        DataWatcherUtil.register(armorStandDataWatcher, 2, "") // custom name
        DataWatcherUtil.register(armorStandDataWatcher, 3, 0.toByte()) // custom name visible
        DataWatcherUtil.register(armorStandDataWatcher, 4, 0.toByte()) // silent
        DataWatcherUtil.setFlag(armorStandDataWatcher, 5, true)
        DataWatcherUtil.register(armorStandDataWatcher, 6, 1.toFloat()) // health
        DataWatcherUtil.register(armorStandDataWatcher, 7, 0)
        DataWatcherUtil.register(armorStandDataWatcher, 8, 0.toByte())
        DataWatcherUtil.register(armorStandDataWatcher, 9, 0.toByte()) // arrow count
        DataWatcherUtil.register(armorStandDataWatcher, 10, 0.toByte())
        DataWatcherUtil.setTypeFlag(armorStandDataWatcher, 10, 1, true)
        DataWatcherUtil.setTypeFlag(armorStandDataWatcher, 10, 4, false)
        DataWatcherUtil.setTypeFlag(armorStandDataWatcher, 10, 8, false)
        DataWatcherUtil.setTypeFlag(armorStandDataWatcher, 10, 10, true)
        DataWatcherUtil.register(armorStandDataWatcher, 11, MinecraftReflection.ARMOR_STAND_HEAD_POSE.clone().vector)
        DataWatcherUtil.register(armorStandDataWatcher, 12, MinecraftReflection.ARMOR_STAND_BODY_POSE.clone().vector)
        DataWatcherUtil.register(armorStandDataWatcher, 13, MinecraftReflection.ARMOR_STAND_LEFT_ARM_POSE.clone().vector)
        DataWatcherUtil.register(armorStandDataWatcher, 14, MinecraftReflection.ARMOR_STAND_RIGHT_ARM_POSE.clone().vector)
        DataWatcherUtil.register(armorStandDataWatcher, 15, MinecraftReflection.ARMOR_STAND_LEFT_LEG_POSE.clone().vector)
        DataWatcherUtil.register(armorStandDataWatcher, 16, MinecraftReflection.ARMOR_STAND_RIGHT_LEG_POSE.clone().vector)

        val armorStandPacket = MinecraftProtocol.newPacket("PacketPlayOutSpawnEntityLiving")
        Reflection.setDeclaredFieldValue(armorStandPacket, "a", armorStandId)
        Reflection.setDeclaredFieldValue(armorStandPacket, "b", 30)
        Reflection.setDeclaredFieldValue(armorStandPacket, "l", armorStandDataWatcher)
        Reflection.setDeclaredFieldValue(armorStandPacket, "c", MinecraftReflection.mathFloor(location.x * 32.0))
        Reflection.setDeclaredFieldValue(armorStandPacket, "d", MinecraftReflection.mathFloor(location.y * 32.0))
        Reflection.setDeclaredFieldValue(armorStandPacket, "e", MinecraftReflection.mathFloor(location.z * 32.0))

        val attachPacket = MinecraftProtocol.newPacket("PacketPlayOutAttachEntity")
        Reflection.setDeclaredFieldValue(attachPacket, "a", 1)
        Reflection.setDeclaredFieldValue(attachPacket, "b", id)
        Reflection.setDeclaredFieldValue(attachPacket, "c", armorStandId)

        MinecraftProtocol.send(player, itemPacket)
        MinecraftProtocol.send(player, itemMetadataPacket)
        MinecraftProtocol.send(player, armorStandPacket)
        MinecraftProtocol.send(player, attachPacket)
    }

    override fun sendRepositionPackets(player: Player) {
        val teleportArmorStandPacket = MinecraftProtocol.newPacket("PacketPlayOutEntityTeleport")
        Reflection.setDeclaredFieldValue(teleportArmorStandPacket, "a", armorStandId)
        Reflection.setDeclaredFieldValue(teleportArmorStandPacket, "b", location.x)
        Reflection.setDeclaredFieldValue(teleportArmorStandPacket, "c", location.y)
        Reflection.setDeclaredFieldValue(teleportArmorStandPacket, "d", location.z)
        Reflection.setDeclaredFieldValue(teleportArmorStandPacket, "e", AngleUtils.yawToBytes(location.yaw ))
        Reflection.setDeclaredFieldValue(teleportArmorStandPacket, "f", AngleUtils.yawToBytes(location.pitch))

        MinecraftProtocol.send(player, teleportArmorStandPacket)
    }

    private inner class EditFloatingItemButton : Button() {
        override fun getName(player: Player): String {
            return "${CC.GREEN}Edit Floating Item"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.addAll(TextSplitter.split(text = "Change the item that is being displayed."))
                desc.add("")
                desc.add("${CC.GREEN}Left-Click to edit item.")
            }
        }

        override fun getMaterial(player: Player): XMaterial
        {
            return XMaterial.ITEM_FRAME
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                SelectItemStackMenu { itemStack ->
                    this@FloatingItem.itemStack = itemStack

                    resendForCurrentWatchers()
                }.openMenu(player)
            }
        }
    }

}
