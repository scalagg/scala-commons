package gg.scala.commons.agnostic.sync

import gg.scala.aware.Aware
import gg.scala.aware.AwareBuilder
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 8/29/2022
 */
object ServerSyncPipeline
{
    @JvmStatic
    val NAMESPACE = "serversync"

    val pipeline by lazy {
        AwareBuilder
            .of<AwareMessage>("serversync")
            .codec(AwareMessageCodec)
            .logger(
                Logger.getLogger("ServerSyncPipeline")
            )
            .build()
    }

    inline fun <T> usePipeline(
        closure: Aware<AwareMessage>.() -> T
    ): T
    {
        return pipeline.closure()
    }
}
