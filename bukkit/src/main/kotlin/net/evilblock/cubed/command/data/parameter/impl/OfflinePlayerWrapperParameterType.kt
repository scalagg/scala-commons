package net.evilblock.cubed.command.data.parameter.impl

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.ArrayList
import org.bukkit.command.CommandSender
import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.util.bukkit.player.OfflinePlayerWrapper

class OfflinePlayerWrapperParameterType : ParameterType<OfflinePlayerWrapper> {

    override fun transform(sender: CommandSender, source: String): OfflinePlayerWrapper {
        return OfflinePlayerWrapper(source)
    }

    override fun tabComplete(sender: Player, flags: Set<String>, source: String): List<String> {
        val completions = ArrayList<String>()
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.canSee(sender)) {
                completions.add(player.name)
            }
        }
        return completions
    }

}