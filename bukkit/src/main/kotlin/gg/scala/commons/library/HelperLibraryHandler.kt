package gg.scala.commons.library

import gg.scala.commons.library.model.Library
import me.lucko.helper.maven.LibraryLoader
import org.bukkit.craftbukkit.libs.jline.internal.Log

/**
 * @author GrowlyX
 * @since 10/14/2021
 */
object HelperLibraryHandler
{

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
                    LibraryLoader.load(
                        instance.groupId,
                        instance.artifactId,
                        instance.version,
                        instance.forcedRepository!!
                    )

                    println("[Cubed] Forced repository found for ${instance.artifactId}")
                } else {
                    var foundRepository = false

                    for (commonRepository in COMMON_REPOSITORIES)
                    {
                        try {
                            LibraryLoader.load(
                                instance.groupId,
                                instance.artifactId,
                                instance.version,
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
                        Log.error("Failed to register ${instance.artifactId} version ${instance.version} due to no matching repository being found.")
                    }
                }
            } else
            {
                Log.info("Failed to load ${field.name} due to it not being an instance of Library.")
            }
        }
    }
}
