package net.evilblock.cubed.feature

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.scala.store.serializer.serializers.GsonSerializer
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
    }
}
