package net.evilblock.cubed.command.data.parameter.impl

import net.evilblock.cubed.command.data.parameter.ParameterType
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot

class EquipmentSlotParameterType : ParameterType<EquipmentSlot> {

    override fun transform(sender: CommandSender, source: String): EquipmentSlot? {
        return try {
            EquipmentSlot.valueOf(source.toUpperCase())
        } catch (e: Exception) {
            sender.sendMessage("${ChatColor.RED}Couldn't find an equipment slot from the given input: ${ChatColor.WHITE}$source")
            null
        }
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return arrayListOf<String>().also { completions ->
            for (slot in EquipmentSlot.values()) {
                if (source.startsWith(slot.name, ignoreCase = true)) {
                    completions.add(slot.name)
                }
            }
        }
    }

}