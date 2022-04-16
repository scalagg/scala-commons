package gg.scala.commons.reflections

import org.bukkit.plugin.Plugin
import org.reflections.Reflections
import org.reflections.scanners.FieldAnnotationsScanner
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.scanners.Scanners
import org.reflections.scanners.TypeAnnotationsScanner
import java.lang.reflect.Method

/**
 * @author GrowlyX
 * @since 4/15/2022
 */
class PackageIndexer(
    pkgName: String
)
{
    val classes by lazy {
        Reflections(
            pkgName,
            MethodAnnotationsScanner(),
            TypeAnnotationsScanner()
        )
    }

    inline fun <reified T : Annotation> getMethodsAnnotatedWith(): List<Method>
    {
        return this.classes
            .get(
                Scanners.MethodsAnnotated
                    .with(T::class.java)
                    .`as`(Method::class.java)
            )
            .toList()
    }

    inline fun <reified T : Annotation> getTypesAnnotatedWith(): List<Class<*>>
    {
        return this.classes
            .getTypesAnnotatedWith(T::class.java)
            .toList()
    }
}
