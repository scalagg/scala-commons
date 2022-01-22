package gg.scala.cloudsync.wrapper

import gg.scala.cloudsync.ScalaCloudSyncConstants
import gg.scala.cloudsync.connection.CloudSyncConnector
import gg.scala.cloudsync.wrapper.repositories.ComponentRepositoryWrapper
import net.evilblock.cubed.serializers.Serializers
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
object Repositories
{
    fun components(): ComponentRepositoryWrapper
    {
        var request = CloudSyncConnector.request(
            ScalaCloudSyncConstants.COMPONENTS
        )

        val wrapper = ComponentRepositoryWrapper()
        var iterated: ComponentRepositoryWrapper? = null

        while (iterated == null || iterated.continuationToken != null)
        {
            if (iterated?.continuationToken != null)
            {
                request = CloudSyncConnector.request(
                    ScalaCloudSyncConstants.COMPONENTS +
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
