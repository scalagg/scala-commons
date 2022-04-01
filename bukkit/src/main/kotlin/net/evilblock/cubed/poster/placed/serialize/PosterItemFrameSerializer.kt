/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.poster.placed.serialize

import com.google.gson.*
import net.evilblock.cubed.serialize.LocationAdapter
import org.bukkit.entity.ItemFrame
import java.lang.reflect.Type

object PosterItemFrameSerializer : JsonSerializer<List<ItemFrame>>, JsonDeserializer<List<ItemFrame>> {

    override fun serialize(src: List<ItemFrame>, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val array = JsonArray()
        for (itemFrame in src) {
            array.add(LocationAdapter.toJson(itemFrame.location))
        }
        return array
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): List<ItemFrame>
    {
        val found = arrayListOf<ItemFrame>()

        val array = json.asJsonArray
        for (element in array) {
            val location = LocationAdapter.fromJson(element.asJsonObject)
            if (location != null) {
                if (!location.chunk.isLoaded) {
                    location.chunk.load()
                }

                val entity = location.world.getEntitiesByClass(ItemFrame::class.java).firstOrNull { it.location == location }
                if (entity != null) {
                    found.add(entity)
                }
            }
        }

        return found
    }

}
