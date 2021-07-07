package net.evilblock.cubed.command.data.parameter.impl

import net.evilblock.cubed.command.data.parameter.ParameterType
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag

class ItemFlagParameterType : ParameterType<ItemFlag> {

    override fun transform(sender: CommandSender, source: String): ItemFlag? {
        return try {
            ItemFlag.valueOf(source.toUpperCase())
        } catch (e: Exception) {
            sender.sendMessage("${ChatColor.RED}Invalid item flag! Try one of the following:")
            sender.sendMessage("${ChatColor.RED}${ItemFlag.values().joinToString()}")
            null
        }
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return arrayListOf<String>().also { completions ->
            for (flag in ItemFlag.values()) {
                if (flag.name.startsWith(source, ignoreCase = true)) {
                    completions.add(flag.name)
                }
            }
        }
    }

}