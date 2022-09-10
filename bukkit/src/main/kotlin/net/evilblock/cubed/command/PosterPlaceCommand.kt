package net.evilblock.cubed.command

import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Name
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.flavor.inject.Inject
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.poster.Poster
import net.evilblock.cubed.poster.PosterHandler
import net.evilblock.cubed.poster.placed.PlacedPoster
import net.evilblock.cubed.poster.placed.PlacedPosterHandler
import net.evilblock.cubed.util.CC
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import java.io.File

@AutoRegister
object PosterPlaceCommand : ScalaCommand()
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    @CommandAlias("place-poster|create-poster")
    @Description("Place a poster")
    @CommandPermission("commons.posters.place")
    fun execute(player: ScalaPlayer, fileName: String)
    {
        val posterFile = File(
            File(plugin.dataFolder, "posters"), fileName
        )

        if (!posterFile.exists())
        {
            throw ConditionFailedException("Couldn't find `/plugins/scala-commons/posters/$fileName`!")
        }

        PlacedPosterHandler.placeInteractionHandler.attachHandler(player.bukkit()) { event ->
            if (event.rightClicked !is ItemFrame)
            {
                return@attachHandler
            }

            event.isCancelled = true

            val itemFrame = event.rightClicked as ItemFrame

            val poster = Poster(fileName, posterFile)
            PosterHandler.trackPoster(poster)
            PosterHandler.saveData()

            val placedPoster = PlacedPoster(poster, false)
            placedPoster.trackItemFrame(itemFrame)
            placedPoster.initializeData()
            placedPoster.setupItemFrame(itemFrame)

            PlacedPosterHandler.trackPlacedPoster(placedPoster)
            PlacedPosterHandler.saveData()

            PlacedPosterHandler.placeInteractionHandler.removeHandler(player.bukkit())

            player.sendMessage("${CC.GREEN}The poster has been placed!")
        }

        player.sendMessage("${CC.GREEN}Right-click an item frame to place the poster!")
    }
}
