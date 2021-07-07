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

object FindUUIDCommand {

    @Command(
        names = ["cubed find uuid"],
        description = "Searches the UUID cache for a UUID by the given username",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "username") username: String) {
        val uuid = Cubed.instance.uuidCache.uuid(username)
        sender.sendMessage("${ChatColor.GOLD}Found UUID: ${ChatColor.GRAY}$uuid")
    }

}