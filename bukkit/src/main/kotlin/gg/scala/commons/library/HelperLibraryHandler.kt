package gg.scala.commons.library

import com.google.common.base.Suppliers
import gg.scala.commons.library.model.Library
import me.lucko.helper.internal.LoaderUtils
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files

/**
 * @author GrowlyX
 * @since 10/14/2021
 */
object HelperLibraryHandler
{

    private val ADD_URL_METHOD = Suppliers.memoize {
        try
        {
            val addUrlMethod = URLClassLoader::class.java
                .getDeclaredMethod("addURL", URL::class.java)
            
            addUrlMethod.isAccessible = true
            return@memoize addUrlMethod
        } catch (e: NoSuchMethodException)
        {
            throw RuntimeException(e)
        }
    }

    /**
     * Common maven repositories, consists
     * of spigot-related repositories.
     */
    @JvmStatic
    val COMMON_REPOSITORIES = listOf(
        "https://jitpack.io",
        "https://repo1.maven.org/maven2",
        "https://repo.dmulloy2.net/nexus/repository/public",
        "https://maven.enginehub.org/repo/",
        "https://hub.spigotmc.org/nexus/content/repositories/snapshots/"
    )

    /**
     * Scans and registers all declared fields in a class
     * which are [Library] instances.
     */
    fun loadAll(clazz: Class<*>, any: Any? = null)
    {
        val fields = clazz.declaredFields

        for (field in fields)
        {
            if (!field.isAccessible)
            {
                field.isAccessible = true
            }

            val instance = field.get(any)
            println("$instance - field")

            if (instance is Library)
            {
                if (instance.forcedRepository != null)
                {
                    internalLoad(
                        instance,
                        instance.forcedRepository!!
                    )

                    println("[Cubed] Forced repository found for ${instance.artifactId}")
                } else {
                    var foundRepository = false

                    for (commonRepository in COMMON_REPOSITORIES)
                    {
                        try {
                            internalLoad(
                                instance,
                                commonRepository
                            )

                            println("[Cubed] Repository found for ${instance.artifactId}")

                            foundRepository = true
                        } catch (exception: Exception)
                        {
                            println("[Cubed] Attempting new repository, for ${instance.artifactId}")
                        }
                    }

                    if (!foundRepository)
                    {
                        println("[helper] Failed to register ${instance.artifactId} version ${instance.version} due to no matching repository being found.")
                    }
                }
            } else
            {
                println("[helper] Failed to load ${field.name} due to it not being an instance of Library.")
            }
        }
    }

    internal fun internalLoad(library: Library, url: String)
    {
        println("[helper] ${
            String.format(
                "Loading dependency %s:%s:%s.",
                library.groupId,
                library.artifactId,
                library.version,
            )
        }")

        val name = library.artifactId + "-" + library.version
        val saveLocation = File(getLibFolder(), "$name.jar")

        if (!saveLocation.exists())
        {
            println("[helper] Dependency '$name' is not already in the libraries folder. Attempting to download...")

            library.getUrl(url).openStream().use { `is` -> Files.copy(`is`, saveLocation.toPath()) }

            println("[helper] Dependency '$name' successfully downloaded.")
        }

        if (!saveLocation.exists())
        {
            throw RuntimeException("Unable to download dependency: $library")
        }

        val classLoader = LoaderUtils.getPlugin().javaClass.classLoader as URLClassLoader

        try
        {
            ADD_URL_METHOD.get().invoke(classLoader, saveLocation.toURI().toURL())
        } catch (e: Exception)
        {
            throw RuntimeException("Unable to load dependency: $saveLocation", e)
        }

        println("[helper] Loaded dependency '$name' successfully.")
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
