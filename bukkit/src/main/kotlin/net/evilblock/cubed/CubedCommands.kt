package net.evilblock.cubed

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.thread.EntityTickThread
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player

object CubedCommands {

    @Command(names = ["cubed reload"], permission = "op")
    @JvmStatic
    fun execute(sender: CommandSender) {
        Cubed.instance.reloadConfig()
        Reflection.DEBUG = Cubed.instance.config.getBoolean("debug", false)
    }

    @Command(names = ["entity debug"], permission = "op")
    @JvmStatic
    fun debug(player: Player) {
        EntityTickThread.DEBUG = !EntityTickThread.DEBUG
        player.sendMessage(EntityTickThread.DEBUG.toString())
    }

    @Command(names = ["entity debug-view"], permission = "op")
    @JvmStatic
    fun debugView(player: Player) {
        if (EntityHandler.toggleDebugView(player)) {
            player.sendMessage("${ChatColor.GREEN}Debug view enabled!")
        } else {
            player.sendMessage("${ChatColor.RED}Debug view disabled!")
        }
    }

    @Command(names = ["spawn-armor-stand"], permission = "op")
    @JvmStatic
    fun spawnArmorStand(player: Player) {
        val armorStand = player.world.spawn(player.location, ArmorStand::class.java)
        armorStand.setBasePlate(false)
        armorStand.setArms(true)
        armorStand.isSmall = true
        armorStand.isMarker = false
    }

    @Command(names = ["item-has-key"], permission = "op")
    @JvmStatic
    fun itemHasKey(player: Player, @Param(name = "key") key: String) {
        val itemInHand = player.inventory.itemInHand
        if (itemInHand == null) {
            player.sendMessage("${ChatColor.RED}You must be holding an item in your hand!")
        } else {
            if (ItemUtils.itemTagHasKey(itemInHand, key)) {
                player.sendMessage("${ChatColor.GREEN}Has key!")
            } else {
                player.sendMessage("${ChatColor.RED}Doesn't have key!")
            }
        }
    }

    @Command(names = ["glow", "item-glow"], permission = "op")
    @JvmStatic
    fun glow(player: Player) {
        val itemInHand = player.inventory.itemInHand
        if (itemInHand == null) {
            player.sendMessage("${ChatColor.RED}You must be holding an item in your hand!")
        } else {
            player.inventory.itemInHand = ItemBuilder.copyOf(itemInHand).glow().build()
        }
    }

}