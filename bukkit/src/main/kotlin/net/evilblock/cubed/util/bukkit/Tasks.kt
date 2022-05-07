/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.bukkit

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service
import net.evilblock.cubed.ScalaCommonsSpigot
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

@Service
object Tasks
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    @JvmStatic
    fun sync(lambda: () -> Unit): BukkitTask
    {
        return plugin.server.scheduler
            .runTask(plugin) {
                lambda.invoke()
            }
    }

    @JvmStatic
    fun delayed(delay: Long, lambda: () -> Unit): BukkitTask
    {
        return plugin.server.scheduler
            .runTaskLater(plugin, {
                lambda.invoke()
            }, delay)
    }

    @JvmStatic
    fun delayed(delay: Long, runnable: Runnable): BukkitTask
    {
        return plugin.server.scheduler
            .runTaskLater(plugin, runnable, delay)
    }

    @JvmStatic
    fun delayed(delay: Long, runnable: BukkitRunnable): BukkitTask
    {
        return runnable.runTaskLater(plugin, delay)
    }

    @JvmStatic
    fun timer(delay: Long, interval: Long, lambda: () -> Unit): BukkitTask
    {
        return plugin.server.scheduler
            .runTaskTimer(plugin, {
                lambda.invoke()
            }, delay, interval)
    }

    @JvmStatic
    fun timer(delay: Long, interval: Long, runnable: Runnable): BukkitTask
    {
        return plugin.server.scheduler
            .runTaskTimer(
                plugin, runnable,
                delay, interval
            )
    }

    @JvmStatic
    fun timer(delay: Long, interval: Long, runnable: BukkitRunnable): BukkitTask
    {
        return runnable.runTaskTimer(plugin, delay, interval)
    }

    @JvmStatic
    fun async(lambda: () -> Unit): BukkitTask
    {
        return plugin.server.scheduler
            .runTaskAsynchronously(plugin) {
                lambda.invoke()
            }
    }

    @JvmStatic
    fun asyncDelayed(delay: Long, lambda: () -> Unit): BukkitTask
    {
        return plugin.server.scheduler
            .runTaskLaterAsynchronously(plugin, {
                lambda.invoke()
            }, delay)
    }

    @JvmStatic
    fun asyncDelayed(runnable: Runnable, delay: Long): BukkitTask
    {
        return plugin.server.scheduler
            .runTaskLaterAsynchronously(
                plugin, runnable, delay
            )
    }

    @JvmStatic
    fun asyncDelayed(runnable: BukkitRunnable, delay: Long): BukkitTask
    {
        return runnable.runTaskLaterAsynchronously(plugin, delay)
    }

    @JvmStatic
    fun asyncTimer(delay: Long, interval: Long, lambda: () -> Unit): BukkitTask
    {
        return plugin.server.scheduler
            .runTaskTimerAsynchronously(plugin, {
                lambda.invoke()
            }, delay, interval)
    }

    @JvmStatic
    fun asyncTimer(runnable: Runnable, delay: Long, interval: Long): BukkitTask
    {
        return plugin.server.scheduler
            .runTaskTimerAsynchronously(
                plugin, runnable,
                delay, interval
            )
    }

    @JvmStatic
    fun asyncTimer(runnable: BukkitRunnable, delay: Long, interval: Long): BukkitTask
    {
        return runnable.runTaskTimerAsynchronously(plugin, delay, interval)
    }
}
