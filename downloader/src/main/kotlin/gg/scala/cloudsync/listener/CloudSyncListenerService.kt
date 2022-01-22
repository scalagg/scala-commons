package gg.scala.cloudsync.listener

import gg.scala.cloudsync.connection.CloudSyncConnector
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import me.lucko.helper.Events
import org.bukkit.event.player.PlayerJoinEvent

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
@Service
object CloudSyncListenerService
{
    @Configure
    fun configure()
    {
        Events.subscribe(PlayerJoinEvent::class.java)
            .filter { it.player.isOp }
            .handler {
                CloudSyncConnector.getTrackedAssetsAsync()
                    .thenApplyAsync {

                    }
            }
    }
}
