/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.poster.placed

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.util.bukkit.selection.impl.EntityInteractionHandler
import java.io.File

object PlacedPosterHandler {

    private val dataFile: File = File(Cubed.instance.dataFolder, "placed-posters.json")
    private val dataType = object : TypeToken<List<PlacedPoster>>() {}.type

    val placeInteractionHandler: EntityInteractionHandler = EntityInteractionHandler("PosterPlace")
    private val placedPosters: MutableList<PlacedPoster> = arrayListOf()

    fun initialLoad() {
        Cubed.instance.server.pluginManager.registerEvents(placeInteractionHandler, Cubed.instance)

        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val placedPosters = Serializers.gson.fromJson(reader, dataType) as List<PlacedPoster>
                for (placedPoster in placedPosters) {
                    try {
                        placedPoster.initializeData()
                        this.placedPosters.add(placedPoster)
                    } catch (e: Exception) {
                        Cubed.instance.logger.severe("Failed to read image of poster: ${placedPoster.poster.imageFile.path}")
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun saveData() {
        Files.write(Serializers.gson.toJson(placedPosters.filter { it.persistent }, dataType), dataFile, Charsets.UTF_8)
    }

    fun onDisable() {
        for (placedPoster in placedPosters) {
            for (itemFrame in placedPoster.getItemFrames()) {
                itemFrame.item = null
            }
        }
    }

    fun getPlacedPosters(): List<PlacedPoster> {
        return placedPosters
    }

    fun trackPlacedPoster(placedPoster: PlacedPoster) {
        placedPosters.add(placedPoster)
    }

    fun forgetPlacedPoster(placedPoster: PlacedPoster) {
        placedPosters.remove(placedPoster)
    }

}