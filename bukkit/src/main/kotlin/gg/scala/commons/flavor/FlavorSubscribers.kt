package gg.scala.commons.flavor

import gg.scala.commons.flavor.annotations.Task
import gg.scala.flavor.Flavor
import me.lucko.helper.Schedulers
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 3/5/2022
 */
object FlavorSubscribers
{
    fun configure(flavor: Flavor)
    {
        flavor.listen<Task> { method, instance ->
            val runnable = Runnable {
                method.invoke(instance)
            }

            val context = method
                .getAnnotation(
                    Task::class.java
                )

            Schedulers.get(context.ctx)
                .runRepeating(
                    runnable,
                    0L, TimeUnit.SECONDS,
                    context.delay, context.unit
                )
        }
    }
}
