package gg.scala.commons.downloader.discovery

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import xyz.mkotb.configapi.ConfigFactory
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
@Service(priority = 45)
object ScalaDownloaderDiscoveryService
{
    @Inject
    lateinit var factory: ConfigFactory

    var discoverable by Delegates
        .notNull<DiscoverableAssets>()

    @Configure
    fun configure()
    {
        discoverable = factory.fromFile(
            "discoverable",
            DiscoverableAssets::class.java
        )
    }

    class DiscoverableAssets(
        val assets: List<String> = listOf(
            "gg.scala.lemon:bukkit:Lemon",
            "net.evilblock.cubed:bukkit:cubed-bukkit",
        )
    )
}
