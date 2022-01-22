package gg.scala.commons.downloader

import gg.scala.commons.downloader.connection.ScalaDownloaderConnector
import gg.scala.commons.downloader.wrapper.repositories.ComponentRepositoryWrapper
import net.evilblock.cubed.serializers.Serializers
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
object Find
{
    fun components(): ComponentRepositoryWrapper
    {
        var request = ScalaDownloaderConnector.request(
            ScalaCommonsDownloaderConstants.COMPONENTS
        )

        val wrapper = ComponentRepositoryWrapper()
        var iterated: ComponentRepositoryWrapper? = null

        while (iterated == null || iterated.continuationToken != null)
        {
            if (iterated?.continuationToken != null)
            {
                request = ScalaDownloaderConnector.request(
                    ScalaCommonsDownloaderConstants.COMPONENTS +
                            "&continuationToken=${iterated.continuationToken}"
                )
            }

            iterated = Serializers.gson
                .fromJson(
                    request.asJson().body.toPrettyString(),
                    ComponentRepositoryWrapper::class.java
                )

            wrapper.items.addAll(iterated.items)
        }

        return wrapper
    }

    fun asyncComponents(): CompletableFuture<ComponentRepositoryWrapper>
    {
        return CompletableFuture
            .supplyAsync { components() }
    }
}
