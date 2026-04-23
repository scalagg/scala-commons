package net.evilblock.cubed.command

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.visibility.VisibilityHandler
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 3/5/2024
 */
@AutoRegister
object VisibilityDebugCommand : ScalaCommand()
{
    @CommandAlias("visibility-debug")
    @CommandPermission("@op")
    fun onVisibilityDebug(player: Player)
    {
        Bukkit.getOnlinePlayers().forEach { other ->
            player.sendMessage("${CC.GRAY}${CC.STRIKE_THROUGH}------------------")
            player.sendMessage(
                VisibilityHandler
                    .getDebugInfo(player, other)
                    .toTypedArray()
            )
        }
    }
}
