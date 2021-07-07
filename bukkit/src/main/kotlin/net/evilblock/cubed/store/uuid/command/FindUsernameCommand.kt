/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.store.uuid.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.util.*

object FindUsernameCommand {

    @Command(
        names = ["cubed find username"],
        description = "Searches the UUID cache for a username by the given uuid",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "uuid") uuid: UUID) {
        val username = Cubed.instance.uuidCache.name(uuid)
        sender.sendMessage("${ChatColor.GOLD}Found username: ${ChatColor.GRAY}$username")
    }

}