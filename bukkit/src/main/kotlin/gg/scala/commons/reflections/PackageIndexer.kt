package gg.scala.commons.reflections

import gg.scala.commons.ScalaCommonsConstants
import org.bukkit.plugin.Plugin
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.scanners.Scanners
import org.reflections.scanners.TypeAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import java.lang.reflect.Method

/**
 * @author GrowlyX
 * @since 4/15/2022
 */
class PackageIndexer(
    plugin: Plugin
)
{
    private val pluginClass =
        plugin::class.java

    val reflections by lazy {
        Reflections(
            ConfigurationBuilder()
                .forPackage(
                    this.pluginClass.`package`.name,
                    this.pluginClass.classLoader
                )

                .addScanners(
                    MethodAnnotationsScanner(),
                    TypeAnnotationsScanner()
                )
        )
    }

    inline fun <reified T : Annotation> getMethodsAnnotatedWith(): List<Method>
    {
        return reflections
            .get(
                ScalaCommonsConstants
                    .annotated<T>()
            )
            .toList()
    }

    inline fun <reified T : Annotation> getTypesAnnotatedWith(): List<Class<*>>
    {
        return reflections
            .getTypesAnnotatedWith(T::class.java)
            .toList()
    }
}
