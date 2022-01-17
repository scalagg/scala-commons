package gg.scala.commons

import gg.scala.commons.library.HelperLibraryHandler
import me.lucko.helper.plugin.ExtendedJavaPlugin
import net.evilblock.cubed.util.ClassUtils
import net.evilblock.cubed.util.bukkit.Tasks

/**
 * An extension of [ExtendedJavaPlugin] with support
 * for [HelperLibraryHandler]'s runtime library installations.
 *
 * @author GrowlyX
 * @since 10/15/2021
 */
open class ExtendedScalaPlugin : ExtendedJavaPlugin()
{
    fun invokeTrackedTask(id: String, lambda: () -> Unit)
    {
        val current = System.currentTimeMillis()
        lambda.invoke()

        logger.info("Finished $id in ${
            System.currentTimeMillis() - current
        }ms!")
    }

    /**
     * Invokes the [lambda] asynchronously and
     * tracks it until its failure/success.
     *
     * If this task takes longer than the [timeout], we
     * will invoke the [onFailure] function synchronously.
     *
     * Status of this tracked task will report on completion
     * in the same fashion as [invokeTrackedTask].
     *
     * @author GrowlyX
     * @since 11/29/2021
     */
    fun invokeTrackedTaskWithTimeout(
        id: String, timeout: Long,
        onCompletion: () -> Unit,
        onFailure: () -> Unit,
        lambda: () -> Unit
    )
    {
        val current = System.currentTimeMillis()
        var complete = false

        Tasks.async {
            lambda.invoke()
            onCompletion.invoke()

            complete = true

            logger.info("Finished $id in ${
                System.currentTimeMillis() - current
            }ms!")
        }

        Tasks.delayed(timeout) {
            if (!complete)
            {
                onFailure.invoke()
            }
        }
    }

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
                    }
                } catch (ignored: Exception)
                {
                    ignored.printStackTrace()
                }
            }
        }
    }
}
