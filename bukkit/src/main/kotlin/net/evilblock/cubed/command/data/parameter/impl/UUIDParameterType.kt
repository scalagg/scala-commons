/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command.data.parameter.impl

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.store.uuidcache.UUIDCache
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.ArrayList
import java.util.UUID
import org.bukkit.command.CommandSender

class UUIDParameterType : ParameterType<UUID?> {

    override fun transform(sender: CommandSender, source: String): UUID? {
        if (sender is Player && (source.equals("self", ignoreCase = true) || source == "")) {
            return sender.uniqueId
        }

        try {
            return UUID.fromString(source)
        } catch (e: Exception) {
            val uuid = Cubed.instance.uuidCache.uuid(source)
            if (uuid != null) {
                return uuid
            } else {
                val optionalProfile = UUIDCache.fetchFromMojang(source)
                if (optionalProfile.isPresent) {
                    Cubed.instance.uuidCache.update(optionalProfile.get().first, optionalProfile.get().second, commit = true)
                    return optionalProfile.get().first
                }
            }
        }

        sender.sendMessage("${ChatColor.RED}Your input did not match a player username or player UUID.")
        return null
    }

    override fun tabComplete(sender: Player, flags: Set<String>, source: String): List<String> {
        val completions = ArrayList<String>()

        for (player in Bukkit.getOnlinePlayers()) {
            // TODO: add support for visibility engine
            if (!sender.canSee(player)) {
                continue
            }

            completions.add(player.name)
        }

        return completions
    }

}