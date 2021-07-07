package net.evilblock.cubed.entity.npc.menu

import net.evilblock.cubed.entity.menu.EditEntityMenu
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.StaticItemStackButton
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.Direction
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.text.TextSplitter
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class EditNpcPositionMenu(private val npc: NpcEntity) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Edit NPC Position"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[3] = LookLeftButton()
            buttons[4] = StaticItemStackButton(ItemStack(Material.SKULL_ITEM, 1, 0))
            buttons[5] = LookRightButton()

            buttons[18] = MoveUpButton()
            buttons[36] = MoveDownButton()

            buttons[22] = MoveForwardButton()
            buttons[30] = MoveLeftButton()
            buttons[32] = MoveRightButton()
            buttons[40] = MoveBackwardButton()

            for (i in 0 until 45) {
                if (!buttons.containsKey(i)) {
                    buttons[i] = GlassButton(7)
                }
            }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 45
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                EditEntityMenu(npc).openMenu(player)
            }
        }
    }

    private inner class MoveUpButton : TexturedHeadButton(Constants.WOOD_ARROW_UP_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Move Up"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.addAll(TextSplitter.split(text = "Pivots the NPC in the upwards direction."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to move up by +0.1"))
                desc.add(styleAction(ChatColor.AQUA, "SHIFT-CLICK", "to move up by +0.25"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                npc.updateLocation(npc.location.clone().add(0.0, 0.1, 0.0))
            } else if (clickType.isShiftClick) {
                npc.updateLocation(npc.location.clone().add(0.0, 0.25, 0.0))
            }
        }
    }

    private inner class MoveDownButton : TexturedHeadButton(Constants.WOOD_ARROW_DOWN_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Move Down"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.addAll(TextSplitter.split(text = "Pivots the NPC in the downwards direction."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to move down by -0.1"))
                desc.add(styleAction(ChatColor.AQUA, "SHIFT-CLICK", "to move down by -0.25"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                npc.updateLocation(npc.location.clone().add(0.0, -0.1, 0.0))
            } else if (clickType.isShiftClick) {
                npc.updateLocation(npc.location.clone().add(0.0, -0.25, 0.0))
            }
        }
    }

    private inner class MoveForwardButton : TexturedHeadButton(Constants.WOOD_ARROW_UP_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Move Forward"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.addAll(TextSplitter.split(text = "Pivots the NPC in the forward direction."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to move forward by +0.1"))
                desc.add(styleAction(ChatColor.AQUA, "SHIFT-CLICK", "to move forward by +0.25"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val mod = when {
                clickType.isLeftClick -> {
                    0.1
                }
                clickType.isShiftClick -> {
                    0.25
                }
                else -> {
                    return
                }
            }

//            when (Direction.getFromYaw(npc.location.yaw)) {
//                Direction.NORTH -> {
//                    npc.updateLocation(npc.location.clone().add())
//                }
//                Direction.EAST -> {
//                    npc.updateLocation(npc.location.clone().add())
//                }
//                Direction.SOUTH -> {
//                    npc.updateLocation(npc.location.clone().add())
//                }
//                Direction.WEST -> {
//                    npc.updateLocation(npc.location.clone().add())
//                }
//            }
        }
    }

    private inner class MoveBackwardButton : TexturedHeadButton(Constants.WOOD_ARROW_DOWN_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Move Backward"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.addAll(TextSplitter.split(text = "Pivots the NPC in the backward direction."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to move backward by -0.1"))
                desc.add(styleAction(ChatColor.AQUA, "SHIFT-CLICK", "to move backward by -0.25"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {

        }
    }

    private inner class MoveLeftButton : TexturedHeadButton(Constants.WOOD_ARROW_LEFT_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Move Left"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.addAll(TextSplitter.split(text = "Pivots the NPC to the left."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to move left by +0.1"))
                desc.add(styleAction(ChatColor.AQUA, "SHIFT-CLICK", "to move left by +0.25"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val mod = when {
                clickType.isLeftClick -> {
                    0.1
                }
                clickType.isShiftClick -> {
                    0.25
                }
                else -> {
                    return
                }
            }

            when (Direction.getFromYaw(npc.location.yaw)) {
                Direction.NORTH -> {
                    npc.updateLocation(npc.location.clone().add(-mod, 0.0, 0.0))
                }
                Direction.EAST -> {
                    npc.updateLocation(npc.location.clone().add(0.0, 0.0, -mod))
                }
                Direction.SOUTH -> {
                    npc.updateLocation(npc.location.clone().add(mod, 0.0, 0.0))
                }
                Direction.WEST -> {
                    npc.updateLocation(npc.location.clone().add(0.0, 0.0, mod))
                }
            }
        }
    }

    private inner class MoveRightButton : TexturedHeadButton(Constants.WOOD_ARROW_RIGHT_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Move Right"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.addAll(TextSplitter.split(text = "Pivots the NPC to the right."))
                desc.add("")
                desc.add(styleAction(ChatColor.GREEN, "LEFT-CLICK", "to move right by +0.1"))
                desc.add(styleAction(ChatColor.AQUA, "SHIFT-CLICK", "to move right by +0.25"))
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val mod = when {
                clickType.isLeftClick -> {
                    0.1
                }
                clickType.isShiftClick -> {
                    0.25
                }
                else -> {
                    return
                }
            }

            when (Direction.getFromYaw(npc.location.yaw)) {
                Direction.NORTH -> {
                    npc.updateLocation(npc.location.clone().add(mod, 0.0, 0.0))
                }
                Direction.EAST -> {
                    npc.updateLocation(npc.location.clone().add(0.0, 0.0, mod))
                }
                Direction.SOUTH -> {
                    npc.updateLocation(npc.location.clone().add(-mod, 0.0, 0.0))
                }
                Direction.WEST -> {
                    npc.updateLocation(npc.location.clone().add(0.0, 0.0, -mod))
                }
            }
        }
    }

    private inner class LookLeftButton : TexturedHeadButton(Constants.WOOD_ARROW_LEFT_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Look Left"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {

            } else if (clickType.isShiftClick) {

            }
        }
    }

    private inner class LookRightButton : TexturedHeadButton(Constants.WOOD_ARROW_RIGHT_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Look Right"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {

            } else if (clickType.isShiftClick) {

            }
        }
    }

//    private inner class EditCoordButton(private val coord: Coord) : TexturedHeadButton() {
//        override fun getName(player: Player): String {
//            return "${ChatColor.YELLOW}${ChatColor.BOLD}Set ${coord.name} Coord"
//        }
//
//        override fun getTexture(player: Player): String {
//            return when (coord) {
//                Coord.X -> {
//                    Constants.WOOD_ARROW_X_TEXTURE
//                }
//                Coord.Y -> {
//                    Constants.WOOD_ARROW_Y_TEXTURE
//                }
//                Coord.Z -> {
//                    Constants.WOOD_ARROW_Z_TEXTURE
//                }
//            }
//        }
//
//        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
//            if (clickType.isLeftClick) {
//                NumberPrompt()
//                    .withText("${ChatColor.GREEN}Please input the new ${coord.name} value.")
//                    .acceptInput { input ->
//                        npc.updateLocation(coord.modify(npc.location, input.toDouble()))
//                        this@EditNpcPositionMenu.openMenu(player)
//                    }
//                    .start(player)
//            }
//        }
//    }
//
//    private enum class Coord {
//        X,
//        Y,
//        Z;
//
//        fun modify(location: Location, value: Double): Location {
//            when (this) {
//                X -> {
//                    location.add(value, 0.0, 0.0)
//                }
//                Y -> {
//                    location.add(0.0, value, 0.0)
//                }
//                Z -> {
//                    location.add(0.0, 0.0, value)
//                }
//            }
//            return location
//        }
//    }

}