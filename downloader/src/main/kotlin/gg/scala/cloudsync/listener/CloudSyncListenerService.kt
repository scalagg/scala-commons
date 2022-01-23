package gg.scala.cloudsync.listener

import gg.scala.cloudsync.ScalaCloudSync
import gg.scala.cloudsync.connection.CloudSyncConnector
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import me.lucko.helper.Events
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.FancyMessage
import org.bukkit.event.player.PlayerJoinEvent

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
@Service
object CloudSyncListenerService
{
    @Inject
    lateinit var plugin: ScalaCloudSync

    @Configure
    fun configure()
    {
        Events.subscribe(PlayerJoinEvent::class.java)
            .filter { it.player.isOp }
            .handler {
                CloudSyncConnector.usePendingUpdates { name, asset, _ ->
                    val fancyMessage = FancyMessage()
                    fancyMessage.withMessage("${CC.D_AQUA}[$name]${CC.YELLOW} You're not up-to-date! The newest version is: ${CC.GREEN}${asset.maven2!!.version}${CC.YELLOW}.")
                    fancyMessage.sendToPlayer(it.player)
                }
            }
            .bindWith(plugin)
    }
}
