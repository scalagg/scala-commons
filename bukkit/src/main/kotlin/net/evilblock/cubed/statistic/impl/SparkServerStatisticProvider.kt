package net.evilblock.cubed.statistic.impl

import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import me.lucko.spark.api.SparkProvider
import me.lucko.spark.api.statistic.StatisticWindow
import net.evilblock.cubed.statistic.ServerStatisticProvider
import net.evilblock.cubed.statistic.ServerStatisticProviders
import java.lang.management.ManagementFactory

/**
 * @author GrowlyX
 * @since 10/8/2021
 */
@Service
@IgnoreAutoScan
@SoftDependency("spark")
class SparkServerStatisticProvider : ServerStatisticProvider
{
    @Configure
    fun configure()
    {
        ServerStatisticProviders.current = this
    }

    private val spark = SparkProvider.get()

    override fun ticksPerSecond(): Double = spark.tps()!!.poll(
        StatisticWindow.TicksPerSecond.SECONDS_5
    )

    override fun cpuUsage(): Double = spark.cpuProcess().poll(
        StatisticWindow.CpuUsage.SECONDS_10
    )

    override fun memoryUsage(): Long
    {
        return ManagementFactory.getMemoryMXBean().heapMemoryUsage.used
    }
}
