package net.evilblock.cubed.feature

import gg.scala.aware.uri.WrappedAwareUri
import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.scala.store.serializer.serializers.GsonSerializer
import gg.scala.store.spigot.ScalaDataStoreSpigot
import net.evilblock.cubed.aware.AwareCloud
import net.evilblock.cubed.serializers.Serializers

/**
 * @author GrowlyX
 * @since 8/23/2022
 */
@Service
@IgnoreAutoScan
object DataStoreFeature
{
    @Configure
    fun configure()
    {
        GsonSerializer
            .provideCustomGson {
                Serializers.gson
            }

        val storeRedis = ScalaDataStoreSpigot
            .INSTANCE.redis

        AwareCloud.uri = WrappedAwareUri(
            storeRedis.hostname, storeRedis.port,
            storeRedis.password, 0
        )
    }
}
