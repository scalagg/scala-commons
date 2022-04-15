package gg.scala.commons.reflections

import net.evilblock.cubed.util.ClassUtils
import org.bukkit.plugin.Plugin
import java.lang.reflect.Method
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 4/15/2022
 */
class Reflections(
    pkgName: String, plugin: Plugin
)
{
    var classes by Delegates.notNull<List<Class<*>>>()

    init
    {
        val initialization =
            System.currentTimeMillis()

        this.classes = ClassUtils
            .getClassesInPackage(plugin, pkgName)
            .toList()

        plugin.logger.info("[Reflect] Cached ${classes.size} classes in ${
            System.currentTimeMillis() - initialization
        }ms.")
    }

    inline fun <reified T : Annotation> getMethodsAnnotatedWith(): List<Method>
    {
        return this.classes.flatMap {
            it.methods.toList()
        }.filter {
            it.getAnnotation(T::class.java) != null
        }
    }

    inline fun <reified T : Annotation> getTypesAnnotatedWith(): List<Class<*>>
    {
        return this.classes.filter {
            it.getAnnotation(T::class.java) != null
        }
    }
}
