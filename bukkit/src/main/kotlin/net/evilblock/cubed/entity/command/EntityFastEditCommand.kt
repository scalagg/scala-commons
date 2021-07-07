package net.evilblock.cubed.entity.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

object EntityFastEditCommand {

    @Command(
        names = ["entity fast-edit", "fe"],
        description = "Toggles fast edit for entities",
        permission = "cubed.entities.manage",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        if (player.hasMetadata("EntityFastEdit")) {
            player.removeMetadata("EntityFastEdit", Cubed.instance)
            player.sendMessage("${ChatColor.RED}Fast edit is now disabled!")
        } else {
            player.setMetadata("EntityFastEdit", FixedMetadataValue(Cubed.instance, true))
            player.sendMessage("${ChatColor.GREEN}Fast edit is now enabled!")
        }
    }

}