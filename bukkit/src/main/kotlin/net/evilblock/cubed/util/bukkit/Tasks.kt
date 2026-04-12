

package net.evilblock.cubed.util.bukkit

import me.lucko.helper.Helper
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

object Tasks
{
    @JvmStatic
    fun sync(lambda: () -> Unit): BukkitTask
    {
        return Helper.hostPlugin().server.scheduler
            .runTask(Helper.hostPlugin()) {
                lambda.invoke()
            }
    }

    @JvmStatic
    fun delayed(delay: Long, lambda: () -> Unit): BukkitTask
    {
        return Helper.hostPlugin().server.scheduler
            .runTaskLater(Helper.hostPlugin(), {
                lambda.invoke()
            }, delay)
    }

    @JvmStatic
    fun delayed(delay: Long, runnable: Runnable): BukkitTask
    {
        return Helper.hostPlugin().server.scheduler
            .runTaskLater(Helper.hostPlugin(), runnable, delay)
    }

    @JvmStatic
    fun delayed(delay: Long, runnable: BukkitRunnable): BukkitTask
    {
        return runnable.runTaskLater(Helper.hostPlugin(), delay)
    }

    @JvmStatic
    fun timer(delay: Long, interval: Long, lambda: () -> Unit): BukkitTask
    {
        return Helper.hostPlugin().server.scheduler
            .runTaskTimer(Helper.hostPlugin(), {
                lambda.invoke()
            }, delay, interval)
    }

    @JvmStatic
    fun timer(delay: Long, interval: Long, runnable: Runnable): BukkitTask
    {
        return Helper.hostPlugin().server.scheduler
            .runTaskTimer(
                Helper.hostPlugin(), runnable,
                delay, interval
            )
    }

    @JvmStatic
    fun timer(delay: Long, interval: Long, runnable: BukkitRunnable): BukkitTask
    {
        return runnable.runTaskTimer(Helper.hostPlugin(), delay, interval)
    }

    @JvmStatic
    fun async(lambda: () -> Unit): BukkitTask
    {
        return Helper.hostPlugin().server.scheduler
            .runTaskAsynchronously(Helper.hostPlugin()) {
                lambda.invoke()
            }
    }

    @JvmStatic
    fun asyncDelayed(delay: Long, lambda: () -> Unit): BukkitTask
    {
        return Helper.hostPlugin().server.scheduler
            .runTaskLaterAsynchronously(Helper.hostPlugin(), {
                lambda.invoke()
            }, delay)
    }

    @JvmStatic
    fun asyncDelayed(runnable: Runnable, delay: Long): BukkitTask
    {
        return Helper.hostPlugin().server.scheduler
            .runTaskLaterAsynchronously(
                Helper.hostPlugin(), runnable, delay
            )
    }

    @JvmStatic
    fun asyncDelayed(runnable: BukkitRunnable, delay: Long): BukkitTask
    {
        return runnable.runTaskLaterAsynchronously(Helper.hostPlugin(), delay)
    }

    @JvmStatic
    fun asyncTimer(delay: Long, interval: Long, lambda: () -> Unit): BukkitTask
    {
        return Helper.hostPlugin().server.scheduler
            .runTaskTimerAsynchronously(Helper.hostPlugin(), {
                lambda.invoke()
            }, delay, interval)
    }

    @JvmStatic
    fun asyncTimer(runnable: Runnable, delay: Long, interval: Long): BukkitTask
    {
        return Helper.hostPlugin().server.scheduler
            .runTaskTimerAsynchronously(
                Helper.hostPlugin(), runnable,
                delay, interval
            )
    }

    @JvmStatic
    fun asyncTimer(runnable: BukkitRunnable, delay: Long, interval: Long): BukkitTask
    {
        return runnable.runTaskTimerAsynchronously(Helper.hostPlugin(), delay, interval)
    }
}
