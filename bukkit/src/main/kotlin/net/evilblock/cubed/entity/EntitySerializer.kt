/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity

import com.google.gson.*
import java.lang.reflect.Type

object EntitySerializer : JsonSerializer<Entity>, JsonDeserializer<Entity> {

    override fun serialize(src: Entity, typeOf: Type, context: JsonSerializationContext): JsonElement {
        val json = JsonObject()
        json.addProperty("type", src::class.java.name)
        json.add("properties", context.serialize(src, src::class.java))
        return json
    }

    override fun deserialize(json: JsonElement, typeOf: Type, context: JsonDeserializationContext): Entity {
        val jsonObject = json.asJsonObject
        val type = jsonObject.get("type").asString
        val properties = jsonObject.get("properties")

        try {
            return context.deserialize<Entity>(properties, Class.forName(type)).also {
                it.initializeData()
                EntityHandler.trackEntity(it)
            }
        } catch (e: ClassNotFoundException) {
            throw JsonParseException("Unknown type: $type", e)
        }
    }

}