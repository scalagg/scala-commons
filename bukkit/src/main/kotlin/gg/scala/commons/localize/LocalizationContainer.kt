package gg.scala.commons.localize

import com.google.common.cache.CacheBuilder
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream

/**
 * @author GrowlyX
 * @since 3/30/2022
 */
class LocalizationContainer(
    private val factory: LocalizationFactory,
    private val context: LocalizationContext,
    private val loader: LocalizationLoader
)
{
    val cache = CacheBuilder.newBuilder()
        .build<String, String>()

    fun validate(): Boolean
    {
        val resourceName = "${
            factory.identifier
        }-${context.name.lowercase()}.${
            loader.fileExtension
        }"

        return File(
            this.factory.directory,
            resourceName
        ).exists()
    }

    fun reload()
    {
        val resourceName = "${
            factory.identifier
        }-${context.name.lowercase()}.${
            loader.fileExtension
        }"

        val resource = File(
            this.factory.directory, resourceName
        )

        val cache = loader
            .load.invoke(resource)

        this.cache.putAll(cache)
    }

    fun save()
    {
        val resourceName = "${
            factory.identifier
        }-${context.name.lowercase()}.${
            loader.fileExtension
        }"

        val resource = File(
            this.factory.directory, resourceName
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
            factory.identifier
        }-${context.name.lowercase()}.${
            loader.fileExtension
        }"

        val resource = File(
            this.factory.directory, resourceName
        )

        if (!resource.exists())
        {
            val template = factory.container.java
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
