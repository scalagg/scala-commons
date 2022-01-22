package gg.scala.cloudsync.listener

import com.google.common.hash.Hashing
import com.google.common.io.Files
import gg.scala.cloudsync.connection.CloudSyncConnector
import gg.scala.cloudsync.discovery.CloudSyncDiscoveryService
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import me.lucko.helper.Events
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.FancyMessage
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import java.io.File

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
        val pluginDirectory = File(
            Bukkit.getWorldContainer(), "plugins"
        )

        Events.subscribe(PlayerJoinEvent::class.java)
            .filter { it.player.isOp }
            .handler {
                CloudSyncConnector.getTrackedAssetsAsync()
                    .thenApplyAsync { wrappers ->
                        wrappers.forEach { component ->
                            val discoverable = CloudSyncDiscoveryService
                                .discoverable.assets
                                .firstOrNull {
                                    it.split(":")[0] == component.group &&
                                            it.split(":")[1] == component.name
                                }
                                ?: return@forEach

                            val asset = component.assets
                                .firstOrNull {
                                    it.maven2 != null &&
                                            it.maven2.extension == "jar"
                                }
                                ?: return@forEach

                            val fileToOverride = discoverable
                                .split(":")[2]

                            val jarFile = "$fileToOverride.jar"

                            val file = pluginDirectory.listFiles()!!
                                .firstOrNull {
                                    it.name.startsWith(jarFile, true)
                                }

                            // creating the file if it
                            // doesn't already exist
                            if (file != null)
                            {
                                // checking if the hashes are the same, so we can save some time
                                // and avoid updating already up-to-date archives
                                val md5Hash = Files
                                    .asByteSource(file)
                                    .hash(Hashing.md5())
                                    .toString()

                                if (md5Hash != asset.checksum.md5)
                                {
                                    val fancyMessage = FancyMessage()
                                    fancyMessage.withMessage("${CC.D_AQUA}[$fileToOverride]${CC.YELLOW} You're not up-to-date! The newest version is: ${CC.GREEN}${asset.maven2!!.version}${CC.YELLOW}.")
                                    fancyMessage.andHoverOf(
                                        "${CC.YELLOW}Current hash: ${CC.WHITE}${md5Hash}",
                                        "${CC.YELLOW}Latest hash: ${CC.GREEN}${asset.checksum.md5}",
                                        "",
                                        "${CC.GRAY}Restart your server to update this plugin."
                                    )

                                    fancyMessage.sendToPlayer(it.player)
                                }
                            }
                        }
                    }
            }
    }
}
