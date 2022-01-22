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
        val request = ScalaDownloaderConnector.request(
            ScalaCommonsDownloaderConstants.COMPONENTS
        )

        return Serializers.gson
            .fromJson(
                request.asJson().body.toPrettyString(),
                ComponentRepositoryWrapper::class.java
            )
    }

    fun asyncComponents(): CompletableFuture<ComponentRepositoryWrapper>
    {
        return CompletableFuture
            .supplyAsync { components() }
    }
}
