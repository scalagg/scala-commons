package gg.scala.commons.library

import com.google.common.base.Suppliers
import gg.scala.commons.library.model.Library
import gg.scala.commons.loader.URLClassLoaderAccess
import me.lucko.helper.internal.LoaderUtils
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 10/14/2021
 */
object HelperLibraryHandler
{
    private val URL_INJECTOR = Suppliers.memoize {
        URLClassLoaderAccess.create(
            LoaderUtils.getPlugin()
                .javaClass.classLoader as URLClassLoader
        )
    }

    /**
     * Scans and registers all declared fields in a class
     * which are [Library] instances.
     */
    fun loadAll(clazz: Class<*>)
    {
        val fields = clazz
            .getAnnotationsByType(Library::class.java)

        for (instance in fields)
        {
            val forced = instance.repository

            internalLoad(
                instance, forced
            )
        }
    }

    private fun internalLoad(library: Library, url: String)
    {
        Logger.getGlobal().info(
            "[Libraries] " + String.format(
                "Loading dependency %s:%s:%s.",
                library.groupId,
                library.artifactId,
                library.version,
            )
        )

        val name = library.artifactId + "-" + library.version
        val saveLocation = File(getLibFolder(), "$name.jar")

        if (!saveLocation.exists())
        {
            getUrl(url, library).openStream().use { `is` -> Files.copy(`is`, saveLocation.toPath()) }

            Logger.getGlobal().info("[Libraries] Dependency '$name' successfully downloaded.")
        }

        if (!saveLocation.exists())
        {
            throw RuntimeException("Unable to download dependency: $library")
        }

        val classLoader = LoaderUtils.getPlugin()
            .javaClass.classLoader as URLClassLoader

        try
        {
            URL_INJECTOR.get().addURL(
                saveLocation.toURI().toURL()
            )
        } catch (e: Exception)
        {
            throw RuntimeException("Unable to load dependency: $saveLocation", e)
        }

        Logger.getGlobal().info("[Libraries] Loaded dependency '$name' successfully.")
    }

    @Throws(MalformedURLException::class)
    fun getUrl(repoUrl: String, library: Library): URL
    {
        var repo: String = repoUrl

        if (!repo.endsWith("/"))
        {
            repo += "/"
        }

        repo += "%s/%s/%s/%s-%s.jar"
        val url = String.format(
            repo,
            library.groupId.replace(".", "/"),
            library.artifactId,
            library.version,
            library.artifactId,
            library.version
        )
        return URL(url)
    }

    private fun getLibFolder(): File
    {
        val pluginDataFolder = LoaderUtils.getPlugin().dataFolder
        val pluginsDir = pluginDataFolder.parentFile
        val helperDir = File(pluginsDir, "helper")
        val libs = File(helperDir, "libraries")
        libs.mkdirs()
        return libs
    }
}
