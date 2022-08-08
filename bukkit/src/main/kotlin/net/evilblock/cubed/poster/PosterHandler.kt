/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.poster

import com.google.common.base.Charsets
import com.google.gson.reflect.TypeToken
import gg.scala.commons.util.Files
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.serializers.Serializers
import java.io.File

@Service
@IgnoreAutoScan
object PosterHandler {

    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    private val dataFile by lazy {
        File(plugin.dataFolder, "posters.json")
    }

    private val dataType = object : TypeToken<List<Poster>>() {}.type

    private val posters: MutableMap<String, Poster> = hashMapOf()

    fun configure() {
        if (dataFile.exists()) {
            Files.read(dataFile) { reader ->
                val posters = Serializers.gson.fromJson(reader, dataType) as List<Poster>
                for (poster in posters) {
                    try {
                        poster.readImage()
                        trackPoster(poster)
                    } catch (e: Exception) {
                        plugin.logger.severe("Failed to read image of poster: ${poster.imageFile.path}")
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun saveData() {
        Files.write(Serializers.gson.toJson(posters.values, dataType), dataFile)
    }

    fun getPosters(): List<Poster> {
        return posters.values.toList()
    }

    fun getPosterById(id: String): Poster? {
        return posters[id.toLowerCase()]
    }

    fun trackPoster(poster: Poster) {
        posters[poster.id.toLowerCase()] = poster
    }

    fun forgetPoster(poster: Poster) {
        posters.remove(poster.id.toLowerCase())
    }

}
