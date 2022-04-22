package gg.scala.commons.scripts

import me.lucko.helper.Schedulers
import me.lucko.scriptcontroller.environment.loader.ScriptLoadingExecutor
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 4/22/2022
 */
object ScriptLoadExecutor : ScriptLoadingExecutor
{
    override fun scheduleAtFixedRate(
        runnable: Runnable, l: Long, timeUnit: TimeUnit
    ): AutoCloseable
    {
        return Schedulers.builder()
            .async()
            .every(l, timeUnit)
            .run(runnable)
    }

    override fun execute(command: Runnable)
    {
        Schedulers.async().run(command)
    }
}
