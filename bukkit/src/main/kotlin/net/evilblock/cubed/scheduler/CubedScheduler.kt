package net.evilblock.cubed.scheduler

import com.google.common.util.concurrent.ThreadFactoryBuilder
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.util.bukkit.Tasks
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 11/29/2021
 */
@Service
object CubedScheduler
{
    val scheduled = mutableMapOf<KClass<*>, MutableList<() -> Unit>>()

    inline fun <reified T> trackScheduledTask(
        noinline lambda: () -> Unit
    )
    {
        scheduled.putIfAbsent(T::class, mutableListOf())
        scheduled[T::class]?.add(lambda)
    }

    @Configure
    fun configure()
    {
        val executor = Executors.newSingleThreadScheduledExecutor(
            ThreadFactoryBuilder()
                .setNameFormat("Cubed - Hour Event Thread")
                .setDaemon(true).build()
        )

        val minOfHour = Calendar.getInstance()[12]
        val minToHour = 60 - minOfHour
        val minToHalfHour = if (minToHour >= 30) minToHour else 30 - minOfHour

        val secOfMinute = Calendar.getInstance().get(Calendar.SECOND)
        val secToMinute = 60 - secOfMinute

        executor.scheduleAtFixedRate(
            {
                scheduled.filter {
                    it.key == AsyncHourMarkEvent::class || it.key == HourMarkEvent::class
                }.forEach {
                    if (it.key == AsyncHourMarkEvent::class)
                    {
                        it.value.forEach { task ->
                            task.invoke()
                        }
                    } else
                    {
                        Tasks.sync {
                            it.value.forEach { task ->
                                task.invoke()
                            }
                        }
                    }
                }
            }, minToHour.toLong(), 60L, TimeUnit.MINUTES
        )

        executor.scheduleAtFixedRate(
            {
                scheduled.filter {
                    it.key == AsyncMinuteMarkEvent::class || it.key == MinuteMarkEvent::class
                }.forEach {
                    if (it.key == AsyncMinuteMarkEvent::class)
                    {
                        it.value.forEach { task ->
                            task.invoke()
                        }
                    } else
                    {
                        Tasks.sync {
                            it.value.forEach { task ->
                                task.invoke()
                            }
                        }
                    }
                }
            }, secToMinute.toLong(), 60L, TimeUnit.SECONDS
        )

        executor.scheduleAtFixedRate(
            {
                scheduled.filter {
                    it.key == AsyncHalfHourMarkEvent::class || it.key == HalfHourMarkEvent::class
                }.forEach {
                    if (it.key == AsyncHalfHourMarkEvent::class)
                    {
                        it.value.forEach { task ->
                            task.invoke()
                        }
                    } else
                    {
                        Tasks.sync {
                            it.value.forEach { task ->
                                task.invoke()
                            }
                        }
                    }
                }
            }, minToHalfHour.toLong(), 30L, TimeUnit.MINUTES
        )
    }
}

class AsyncHourMarkEvent : ScheduledEventTracker
class AsyncHalfHourMarkEvent : ScheduledEventTracker
class AsyncMinuteMarkEvent : ScheduledEventTracker

class HourMarkEvent : ScheduledEventTracker
{
    override fun async(): Boolean = false
}
class HalfHourMarkEvent : ScheduledEventTracker
{
    override fun async(): Boolean = false
}
class MinuteMarkEvent : ScheduledEventTracker
{
    override fun async(): Boolean = false
}

interface ScheduledEventTracker
{
    fun async(): Boolean = true
}
