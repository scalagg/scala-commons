/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.poster

import com.google.gson.*
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.reflect.Type
import javax.imageio.ImageIO

class Poster(
    val id: String,
    internal var imageFile: File
) {

    @Transient
    private var image: BufferedImage? = null

    fun getBuffer(): BufferedImage? {
        return image
    }

    @Throws(FileNotFoundException::class, IOException::class)
    fun readImage() {
        if (!imageFile.exists()) {
            throw FileNotFoundException("Image file does not exist")
        }

        image = ImageIO.read(imageFile.toURI().toURL())

        PosterUtils.resizeImage(image!!)
    }

    class ReferenceSerializer : JsonSerializer<Poster>, JsonDeserializer<Poster> {
        override fun serialize(src: Poster, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.id)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Poster? {
            return PosterHandler.getPosterById(json.asString)
        }
    }

}