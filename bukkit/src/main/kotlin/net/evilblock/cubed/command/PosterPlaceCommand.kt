package net.evilblock.cubed.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Name
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.poster.Poster
import net.evilblock.cubed.poster.PosterHandler
import net.evilblock.cubed.poster.placed.PlacedPoster
import net.evilblock.cubed.poster.placed.PlacedPosterHandler
import net.evilblock.cubed.util.CC
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import java.io.File

object PosterPlaceCommand : BaseCommand() {

    @CommandAlias("place-poster|create-poster")
    @Description("Place a poster")
    @CommandPermission("cubed.posters.place")
    fun execute(player: Player, @Name("fileName") fileName: String) {
        val posterFile = File(File(Cubed.instance.dataFolder, "posters"), fileName)
        if (!posterFile.exists()) {
            throw ConditionFailedException("Couldn't find `/plugins/Cubed/posters/$fileName`!")
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

            player.sendMessage("${CC.GREEN}The poster has been placed!")
        }

        player.sendMessage("${CC.GREEN}Right-click an item frame to place the poster!")
    }
}