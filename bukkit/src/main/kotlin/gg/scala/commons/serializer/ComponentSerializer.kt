package gg.scala.commons.serializer

import com.google.gson.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import java.lang.reflect.Type

/**
 * @author GrowlyX
 * @since 4/25/2022
 */
object ComponentSerializer : JsonDeserializer<Component>, JsonSerializer<Component>
{
    val downSampling = GsonComponentSerializer
        .colorDownsamplingGson()

    override fun deserialize(
        p0: JsonElement, p1: Type, p2: JsonDeserializationContext
    ): Component
    {
        return downSampling
            .deserializeFromTree(p0)
    }

    override fun serialize(
        p0: Component,
        p1: Type,
        p2: JsonSerializationContext
    ): JsonElement
    {
        return downSampling
            .serializeToTree(p0)
    }
}
