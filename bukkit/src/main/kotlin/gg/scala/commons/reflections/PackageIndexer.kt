package gg.scala.commons.reflections

import org.bukkit.plugin.Plugin
import org.reflections.Reflections
import java.lang.reflect.Method
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.kotlinFunction

/**
 * @author GrowlyX
 * @since 4/15/2022
 */
class PackageIndexer(
    pkgName: String, plugin: Plugin
)
{
    val classes by lazy {
        plugin.logger.info("[Reflect] Scanning \"$pkgName\" for classes...")
        Reflections(pkgName)
    }

    inline fun <reified T : Annotation> getMethodsAnnotatedWith(): List<Method>
    {
        return this.classes.allTypes
            .map {
                Class.forName(it)
            }
            .flatMap {
                it.methods.toList()
            }
            .filter {
                it.kotlinFunction
                    ?.hasAnnotation<T>()
                    ?: it
                        .isAnnotationPresent(
                            T::class.java
                        )
            }.apply {
                if (this.isEmpty())
                {
                    println("Empty method ${T::class.java.name}")
                } else
                {
                    println("indexing: ${T::class.java.name}")

                    this.onEach {
                        println(" ${it.name}")
                    }
                }
            }
    }

    inline fun <reified T : Annotation> getTypesAnnotatedWith(): List<Class<*>>
    {
        return this.classes
            .getTypesAnnotatedWith(T::class.java)
            .toList().apply {
                if (this.isEmpty())
                {
                    println("Empty")
                } else
                {
                    println("indexing: ${T::class.java.name}")

                    this.onEach {
                        println(" ${it.name}")
                    }
                }
            }
    }
}
