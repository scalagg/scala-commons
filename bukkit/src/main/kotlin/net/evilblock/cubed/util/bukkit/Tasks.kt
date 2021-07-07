/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.bukkit

import net.evilblock.cubed.Cubed
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

object Tasks {

    @JvmStatic
    fun sync(lambda: () -> Unit): BukkitTask {
        return Cubed.instance.server.scheduler.runTask(Cubed.instance) {
            lambda.invoke()
        }
    }

    @JvmStatic
    fun delayed(delay: Long, lambda: () -> Unit): BukkitTask {
        return Cubed.instance.server.scheduler.runTaskLater(Cubed.instance, {
            lambda.invoke()
        }, delay)
    }

    @JvmStatic
    fun delayed(delay: Long, runnable: Runnable): BukkitTask {
        return Cubed.instance.server.scheduler.runTaskLater(Cubed.instance, runnable, delay)
    }

    @JvmStatic
    fun delayed(delay: Long, runnable: BukkitRunnable): BukkitTask {
        return runnable.runTaskLater(Cubed.instance, delay)
    }

    @JvmStatic
    fun timer(delay: Long, interval: Long, lambda: () -> Unit): BukkitTask {
        return Cubed.instance.server.scheduler.runTaskTimer(Cubed.instance, {
            lambda.invoke()
        }, delay, interval)
    }

    @JvmStatic
    fun timer(delay: Long, interval: Long, runnable: Runnable): BukkitTask {
        return Cubed.instance.server.scheduler.runTaskTimer(Cubed.instance, runnable, delay, interval)
    }

    @JvmStatic
    fun timer(delay: Long, interval: Long, runnable: BukkitRunnable): BukkitTask {
        return runnable.runTaskTimer(Cubed.instance, delay, interval)
    }

    @JvmStatic
    fun async(lambda: () -> Unit): BukkitTask {
        return Cubed.instance.server.scheduler.runTaskAsynchronously(Cubed.instance) {
            lambda.invoke()
        }
    }

    @JvmStatic
    fun asyncDelayed(delay: Long, lambda: () -> Unit): BukkitTask {
        return Cubed.instance.server.scheduler.runTaskLaterAsynchronously(Cubed.instance, {
            lambda.invoke()
        }, delay)
    }

    @JvmStatic
    fun asyncDelayed(runnable: Runnable, delay: Long): BukkitTask {
        return Cubed.instance.server.scheduler.runTaskLaterAsynchronously(Cubed.instance, runnable, delay)
    }

    @JvmStatic
    fun asyncDelayed(runnable: BukkitRunnable, delay: Long): BukkitTask {
        return runnable.runTaskLaterAsynchronously(Cubed.instance, delay)
    }

    @JvmStatic
    fun asyncTimer(delay: Long, interval: Long, lambda: () -> Unit): BukkitTask {
        return Cubed.instance.server.scheduler.runTaskTimerAsynchronously(Cubed.instance, {
            lambda.invoke()
        }, delay, interval)
    }

    @JvmStatic
    fun asyncTimer(runnable: Runnable, delay: Long, interval: Long): BukkitTask {
        return Cubed.instance.server.scheduler.runTaskTimerAsynchronously(Cubed.instance, runnable, delay, interval)
    }

    @JvmStatic
    fun asyncTimer(runnable: BukkitRunnable, delay: Long, interval: Long): BukkitTask {
        return runnable.runTaskTimerAsynchronously(Cubed.instance, delay, interval)
    }

}