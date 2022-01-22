package gg.scala.commons.downloader.listener

import gg.scala.commons.downloader.connection.ScalaDownloaderConnector
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import me.lucko.helper.Events
import org.bukkit.event.player.PlayerJoinEvent

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
@Service
object ScalaDownloaderListenerService
{
    @Configure
    fun configure()
    {
        Events.subscribe(PlayerJoinEvent::class.java)
            .filter { it.player.isOp }
            .handler {
                ScalaDownloaderConnector.getTrackedAssetsAsync()
                    .thenApplyAsync {

                    }
            }
    }
}
