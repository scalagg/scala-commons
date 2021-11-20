package gg.scala.commons

import gg.scala.commons.library.HelperLibraryHandler
import me.lucko.helper.plugin.ExtendedJavaPlugin
import net.evilblock.cubed.util.ClassUtils

/**
 * An extension of [ExtendedJavaPlugin] with support
 * for [HelperLibraryHandler]'s runtime library installations.
 *
 * @author GrowlyX
 * @since 10/15/2021
 */
open class ExtendedScalaPlugin : ExtendedJavaPlugin()
{
    override fun load()
    {
        val `package` = description.main.replace(
            this.javaClass.simpleName, "library"
        )

        val classes = ClassUtils.getClassesInPackage(this, `package`)

        if (classes.isNotEmpty())
        {
            classes.forEach { clazz ->
                try
                {
                    val instance = clazz.fields.firstOrNull {
                        it.name == "INSTANCE"
                    }

                    if (instance != null)
                    {
                        instance.isAccessible = true

                        HelperLibraryHandler.loadAll(
                            instance.get(null).javaClass
                        )

                        println("[Cubed] Attempting to load ${clazz.simpleName}...")
                    }
                } catch (ignored: Exception)
                {
                    ignored.printStackTrace()
                }
            }
        }
    }
}
