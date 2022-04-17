package gg.scala.commons.localization.container

import com.google.common.cache.CacheBuilder
import gg.scala.commons.localization.LocalizationContext
import gg.scala.commons.localization.LocalizationLoader
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream

/**
 * @author GrowlyX
 * @since 3/30/2022
 */
@Suppress("UNCHECKED_CAST")
class LocalizationContainer(
    private val contextual: LocalizationContainerContextual,
    private val context: LocalizationContext,
    private val loader: LocalizationLoader
)
{
    val cache = CacheBuilder.newBuilder()
        .build<String, Any>()

    val miniMessage = MiniMessage
        .miniMessage()

    fun getString(id: String): String
    {
        return cache.get(id) { "" } as String
    }

    fun getList(id: String): String
    {
        return cache.get(id) { "" } as String
    }

    fun getComponent(
        id: String,
        vararg replacements: Pair<String, String>
    ): Component
    {
        val value = this.cache
            .get(id) { null }
            ?: return Component.empty()

        if (value is List<*>)
        {
            val list = value as List<String>

            val component =
                Component.empty()

            for (message in list)
            {
                var newMessage = message

                for (replacement in replacements)
                {
                    newMessage = newMessage.replace(
                        replacement.first, replacement.second
                    )
                }

                component.append(
                    Component.newline()
                )

                component.append(
                    this.miniMessage.deserialize(newMessage)
                )
            }

            return component
        } else if (value is String)
        {
            val component =
                Component.empty()

            var newMessage = value as String

            for (replacement in replacements)
            {
                newMessage = newMessage.replace(
                    replacement.first, replacement.second
                )
            }

            component.append(
                this.miniMessage.deserialize(newMessage)
            )

            return component
        }

        return Component.empty()
    }

    fun reload()
    {
        val resourceName = "${
            contextual.identifier
        }-${context.name.lowercase()}.${
            loader.fileExtension
        }"

        val resource = File(
            this.contextual.directory, resourceName
        )

        val cache = loader
            .load.invoke(resource)

        this.cache.putAll(cache)
    }

    fun save()
    {
        val resourceName = "${
            contextual.identifier
        }-${context.name.lowercase()}.${
            loader.fileExtension
        }"

        val resource = File(
            this.contextual.directory, resourceName
        )

        if (!resource.exists())
        {
            resource.createNewFile()
        }

        this.loader.save
            .invoke(
                cache.asMap(), resource
            )
    }

    fun preLoad()
    {
        val resourceName = "${
            contextual.identifier
        }-${context.name.lowercase()}.${
            loader.fileExtension
        }"

        val resource = File(
            this.contextual.directory, resourceName
        )

        if (!resource.exists())
        {
            val template = contextual.container.java
                .getResourceAsStream(
                    "localized/$resourceName"
                )!!

            val outStream =
                FileOutputStream(resource)

            outStream.write(
                template.buffered().readBytes()
            )

            IOUtils.closeQuietly(outStream)

            loader.loadReader.invoke(
                template.reader()
            )

            return
        }

        val cache = loader
            .load.invoke(resource)

        this.cache.putAll(cache)
    }
}
