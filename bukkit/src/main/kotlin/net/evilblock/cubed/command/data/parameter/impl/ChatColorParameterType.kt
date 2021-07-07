package net.evilblock.cubed.command.data.parameter.impl

import net.evilblock.cubed.command.data.parameter.ParameterType
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ChatColorParameterType : ParameterType<ChatColor> {

    override fun transform(sender: CommandSender, source: String): ChatColor? {
        return try {
            ChatColor.valueOf(source.toUpperCase())
        } catch (e: Exception) {
            sender.sendMessage("${ChatColor.RED}Couldn't parse `$source` to a color. Try using tab-complete.")
            null
        }
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return ChatColor.values().map { it.name }
    }

}