package gg.scala.commons.library

import com.google.common.base.Suppliers
import gg.scala.commons.library.model.Library
import gg.scala.commons.loader.URLClassLoaderAccess
import me.lucko.helper.internal.LoaderUtils
import net.evilblock.cubed.ScalaCommonsSpigot
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
            "[Injector] " + String.format(
                "Loading dependency %s:%s:%s.",
                library.groupId,
                library.artifactId,
                library.version,
            )
        )

        val name = library.artifactId + "-" + library.version + if (library.shadedClassifier) "-shaded" else ""
        val saveLocation = File(getLibFolder(), "$name.jar")

        if (!saveLocation.exists())
        {
            getUrl(url, library).openStream().use { `is` -> Files.copy(`is`, saveLocation.toPath()) }

            Logger.getGlobal().info("[Injector] Dependency \"$name\" successfully downloaded.")
        }

        if (!saveLocation.exists())
        {
            throw RuntimeException("Unable to download dependency: $library")
        }

        try
        {
            URL_INJECTOR.get().addURL(
                saveLocation.toURI().toURL()
            )
        } catch (exception: Exception)
        {
            throw RuntimeException("Unable to load dependency: $saveLocation", exception)
        }

        Logger.getGlobal().info("[Injector] Loaded dependency \"$name\" successfully.")
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
            if (library.shadedClassifier)
                "${library.version}-shaded" else library.version
        )
        return URL(url)
    }

    private fun getLibFolder(): File
    {
        val file = File(
            ScalaCommonsSpigot.instance.dataFolder,
            "dependencies"
        )
        file.mkdirs()

        return file
    }
}
