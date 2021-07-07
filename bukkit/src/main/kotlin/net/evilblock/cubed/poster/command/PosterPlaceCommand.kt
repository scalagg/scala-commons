package net.evilblock.cubed.poster.command

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.poster.Poster
import net.evilblock.cubed.poster.PosterHandler
import net.evilblock.cubed.poster.placed.PlacedPoster
import net.evilblock.cubed.poster.placed.PlacedPosterHandler
import org.bukkit.ChatColor
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import java.io.File

object PosterPlaceCommand {

    @Command(
        names = ["poster place", "poster create"],
        description = "Place a poster",
        permission = "posters.place"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "fileName") fileName: String) {
        val posterFile = File(File(Cubed.instance.dataFolder, "posters"), fileName)
        if (!posterFile.exists()) {
            player.sendMessage("${ChatColor.RED}Couldn't find `/plugins/Cubed/posters/$fileName`!")
            return
        }

        PlacedPosterHandler.placeInteractionHandler.attachHandler(player) { event ->
            if (event.rightClicked !is ItemFrame) {
                return@attachHandler
            }

            event.isCancelled = true

            val itemFrame = event.rightClicked as ItemFrame

            val poster = Poster(fileName, posterFile)
            PosterHandler.trackPoster(poster)
            PosterHandler.saveData()

            val placedPoster = PlacedPoster(poster)
            placedPoster.trackItemFrame(itemFrame)
            placedPoster.initializeData()
            placedPoster.setupItemFrame(itemFrame)

            PlacedPosterHandler.trackPlacedPoster(placedPoster)
            PlacedPosterHandler.saveData()

            PlacedPosterHandler.placeInteractionHandler.removeHandler(player)

            player.sendMessage("${ChatColor.GREEN}The poster has been placed!")
        }

        player.sendMessage("${ChatColor.GREEN}Right-click an item frame to place the poster!")
    }

}