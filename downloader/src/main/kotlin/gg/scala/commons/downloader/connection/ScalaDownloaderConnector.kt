package gg.scala.commons.downloader.connection

import com.google.common.hash.Hashing
import com.google.common.io.Files
import gg.scala.commons.downloader.Find
import gg.scala.commons.downloader.ScalaCommonsDownloader
import gg.scala.commons.downloader.authentication.ScalaDownloaderAuthenticationService
import gg.scala.commons.downloader.discovery.ScalaDownloaderDiscoveryService
import gg.scala.commons.downloader.wrapper.component.ComponentWrapper
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Close
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import kong.unirest.GetRequest
import kong.unirest.Unirest
import kong.unirest.UnirestInstance
import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import java.io.File
import java.net.URL
import java.util.concurrent.CompletableFuture
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
@Service(priority = 40)
object ScalaDownloaderConnector
{
    @Inject
    lateinit var plugin: ScalaCommonsDownloader

    var unirest by Delegates
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
            println("=========================")
            println("===debug: " + component.group)

            val discoverable = ScalaDownloaderDiscoveryService
                .discoverable.assets
                .firstOrNull {
                    it.split(":")[0] == component.group &&
                            it.split(":")[0] == component.name
                }
                ?: return@forEach

            println("===debug: Passed discovery check")

            val asset = component.assets
                .firstOrNull {
                    it.maven2 != null &&
                            it.maven2.extension == "jar"
                }
                ?: return@forEach

            println("===debug: Passed jar check")

            val fileToOverride = discoverable
                .split(":")[2]

            val file = pluginDirectory.listFiles()!!
                .firstOrNull {
                    it.name.startsWith(fileToOverride, true)
                }
                ?: File(pluginDirectory, "$fileToOverride.jar")

            // creating the file if it
            // doesn't already exist
            if (!file.exists())
            {
                println("===debug: file not found, creating")
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

            println("===debug: got through checksum checks")

            val downloadUrl = URL(asset.downloadUrl)

            val start = System.currentTimeMillis()
            plugin.logger.info("Downloading the latest $fileToOverride archive...")

            try
            {
                FileUtils.copyURLToFile(
                    downloadUrl, file,
                    1000, 1000
                )

                plugin.logger.info("Successfully updated $fileToOverride (took ${
                    System.currentTimeMillis() - start
                }ms)!")
            } catch (exception: Exception)
            {
                plugin.logger.warning("Oops, seems like an exception was thrown during the update process. (${exception.message})")
            }

            println("===debug: got through file stuff")
            println("============================")
        }
    }

    fun getTrackedAssetsAsync(): CompletableFuture<List<ComponentWrapper>>
    {
        return Find.asyncComponents().thenApply { request ->
            val assets = mutableListOf<ComponentWrapper>()

            for (component in request.items)
            {
                println("=========================")
                println("===debug: " + component.group)
                // Checks if this asset should be updated
                ScalaDownloaderDiscoveryService
                    .discoverable.assets
                    .firstOrNull {
                        it.split(":")[0] == component.group &&
                                it.split(":")[0] == component.name
                    }
                    ?: continue

                println("===debug: Passed asset check")

                // Find the jar archive asset we need
                component.assets
                    .firstOrNull {
                        it.maven2 != null &&
                                it.maven2.extension == "jar"
                    }
                    ?: continue

                println("===debug: Passed JAR check")

                assets.add(component)
            }

            return@thenApply assets
        }
    }

    private fun getTrackedAssets(): List<ComponentWrapper>
    {
        val assets = mutableListOf<ComponentWrapper>()

        val request = Find.components()
        println(request.items.joinToString {
            it.group
        })

        for (component in request.items)
        {
            println("=========================")
            println("===debug: " + component.group)
            // Checks if this asset should be updated
            ScalaDownloaderDiscoveryService
                .discoverable.assets
                .firstOrNull {
                    it.split(":")[0] == component.group &&
                            it.split(":")[0] == component.name
                }
                ?: continue

            println("===debug: Passed asset check")

            // Find the jar archive asset we need
            component.assets
                .firstOrNull {
                    it.maven2 != null &&
                            it.maven2.extension == "jar"
                }
                ?: continue

            println("===debug: Passed JAR check")

            assets.add(component)
        }

        return assets
    }

    fun request(request: String): GetRequest
    {
        val wrapper =
            ScalaDownloaderAuthenticationService.getWrapper()

        return unirest.get(request)
            .basicAuth(wrapper.username, wrapper.password)
            .connectTimeout(1000)
    }
}
