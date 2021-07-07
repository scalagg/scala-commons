/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.serializers.impl

import com.google.gson.*
import java.lang.reflect.Type
import java.util.*

class UUIDAdapter : JsonDeserializer<UUID>, JsonSerializer<UUID> {

    @Throws(JsonParseException::class)
    override fun deserialize(src: JsonElement, type: Type, context: JsonDeserializationContext): UUID {
        return UUID.fromString(src.asString)
    }

    override fun serialize(src: UUID, type: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toString())
    }

}
