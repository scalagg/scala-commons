package gg.scala.cloudsync.connection

import com.google.common.hash.Hashing
import com.google.common.io.Files
import gg.scala.cloudsync.wrapper.Repositories
import gg.scala.cloudsync.ScalaCloudSync
import gg.scala.cloudsync.authentication.CloudSyncAuthenticationService
import gg.scala.cloudsync.discovery.CloudSyncDiscoveryService
import gg.scala.cloudsync.wrapper.component.ComponentWrapper
import gg.scala.cloudsync.wrapper.component.asset.ComponentAssetWrapper
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Close
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import kong.unirest.GetRequest
import kong.unirest.Unirest
import kong.unirest.UnirestInstance
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.FancyMessage
import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.logging.Level
import kotlin.properties.Delegates


/**
 * @author GrowlyX
 * @since 1/21/2022
 */
@Service(priority = 40)
object CloudSyncConnector
{
    @Inject
    lateinit var plugin: ScalaCloudSync

    private var unirest by Delegates
        .notNull<UnirestInstance>()

    @Configure
    fun configure()
    {
        unirest = Unirest.primaryInstance()
    }

    @Close
    fun close()
    {
        // Get the plugins directory
        val pluginDirectory = File(
            Bukkit.getWorldContainer(), "plugins"
        )

        getTrackedAssets().forEach { component ->
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
                    it.name.equals(jarFile, true)
                }
                ?: File(pluginDirectory, jarFile)

            // creating the file if it
            // doesn't already exist
            if (!file.exists())
            {
                file.createNewFile()
            } else
            {
                // checking if the hashes are the same, so we can save some time
                // and avoid updating already up-to-date archives
                val md5Hash = Files
                    .asByteSource(file)
                    .hash(Hashing.md5())
                    .toString()

                if (md5Hash == asset.checksum.md5)
                {
                    plugin.logger.info("You're running the latest version of $fileToOverride!")
                    return@forEach
                }
            }

            val start = System.currentTimeMillis()
            plugin.logger.info("Downloading the latest $fileToOverride archive...")

            try
            {
                request(asset.downloadUrl).asObject {
                    FileUtils.copyInputStreamToFile(
                        it.content, file
                    )
                }

                plugin.logger.warning(
                    "Successfully updated $fileToOverride (took ${
                        System.currentTimeMillis() - start
                    }ms)!"
                )
            } catch (exception: Exception)
            {
                plugin.logger.log(
                    Level.SEVERE,
                    "Oops, seems like an exception was thrown during the update process.",
                    exception
                )
            }
        }
    }

    fun getTrackedAssetsAsync(): CompletableFuture<List<ComponentWrapper>>
    {
        return Repositories.asyncComponents().thenApply { request ->
            val assets = mutableListOf<ComponentWrapper>()

            for (component in request.items)
            {
                // Checks if this asset should be updated
                CloudSyncDiscoveryService
                    .discoverable.assets
                    .firstOrNull {
                        it.split(":")[0] == component.group &&
                                it.split(":")[1] == component.name
                    }
                    ?: continue

                // Find the jar archive asset we need
                component.assets
                    .firstOrNull {
                        it.maven2 != null &&
                                it.maven2.extension == "jar"
                    }
                    ?: continue

                assets.add(component)
            }

            return@thenApply assets
        }
    }

    private fun getTrackedAssets(): List<ComponentWrapper>
    {
        val assets = mutableListOf<ComponentWrapper>()
        val request = Repositories.components()

        for (component in request.items)
        {
            // Checks if this asset should be updated
            CloudSyncDiscoveryService
                .discoverable.assets
                .firstOrNull {
                    it.split(":")[0] == component.group &&
                            it.split(":")[1] == component.name
                }
                ?: continue

            // Find the jar archive asset we need
            component.assets
                .firstOrNull {
                    it.maven2 != null &&
                            it.maven2.extension == "jar"
                }
                ?: continue

            assets.add(component)
        }

        return assets
    }

    fun usePendingUpdates(
        lambda: (String, ComponentAssetWrapper, ComponentWrapper) -> Unit
    )
    {
        val pluginDirectory = File(
            Bukkit.getWorldContainer(), "plugins"
        )

        getTrackedAssetsAsync().thenApplyAsync { wrappers ->
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
                        it.name.equals(jarFile, true)
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
                        lambda.invoke(fileToOverride, asset, component)
                    }
                }
            }
        }
    }

    fun request(request: String): GetRequest
    {
        val wrapper =
            CloudSyncAuthenticationService.getWrapper()

        return unirest.get(request)
            .basicAuth(wrapper.username, wrapper.password)
            .connectTimeout(1000)
    }
}
