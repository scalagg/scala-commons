/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.hologram.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.flag.Flag
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.cubed.entity.hologram.updating.FormatUpdatingHologramEntity
import net.evilblock.cubed.util.text.TextUtil
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object HologramCreateCommand {

    @Command(
        names = ["hologram create", "holo create"],
        description = "Create a hologram",
        permission = "cubed.holograms.create"
    )
    @JvmStatic
    fun execute(
        player: Player,
        @Flag(value = ["u", "updating"], description = "If the hologram should auto-update", defaultValue = false) updating: Boolean,
        @Param(name = "text", wildcard = true) text: String
    ) {
        val hologramText = if (TextUtil.isBlank(text = text)) "" else text

        val hologram = if (updating) {
            FormatUpdatingHologramEntity(text = ChatColor.translateAlternateColorCodes('&', hologramText), location = player.eyeLocation)
        } else {
            HologramEntity(text = ChatColor.translateAlternateColorCodes('&', hologramText), location = player.eyeLocation)
        }

        hologram.initializeData()

        EntityHandler.trackEntity(hologram)
        EntityHandler.saveData()

        player.sendMessage("${ChatColor.GREEN}Successfully created hologram!")
    }

}