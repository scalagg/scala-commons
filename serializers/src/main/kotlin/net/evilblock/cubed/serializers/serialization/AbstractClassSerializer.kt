package net.evilblock.cubed.serializers.serialization

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * @author GrowlyX
 * @since 8/30/2022
 */
class AbstractClassSerializer : JsonSerializer<Any?>, JsonDeserializer<Any?>
{
    @Throws(JsonParseException::class)
    override fun deserialize(
        jsonElement: JsonElement, type: Type?,
        jsonDeserializationContext: JsonDeserializationContext
    ): Any
    {
        val jsonObj = jsonElement.asJsonObject
        val className = jsonObj.get(CLASS_META_KEY).asString

        return try
        {
            jsonDeserializationContext.deserialize(
                jsonElement, Class.forName(className)
            )
        } catch (exception: ClassNotFoundException)
        {
            throw JsonParseException(exception)
        }
    }

    override fun serialize(
        `object`: Any?, type: Type?,
        jsonSerializationContext: JsonSerializationContext
    ): JsonElement
    {
        if (`object` != null)
        {
            val jsonElement = jsonSerializationContext
                .serialize(
                    `object`, `object`.javaClass
                )

            jsonElement.asJsonObject.addProperty(
                CLASS_META_KEY,
                `object`.javaClass.canonicalName
            )

            return jsonElement
        }

        return JsonObject()
    }

    companion object
    {
        private const val CLASS_META_KEY = "CLASS_METADATA"
    }
}
