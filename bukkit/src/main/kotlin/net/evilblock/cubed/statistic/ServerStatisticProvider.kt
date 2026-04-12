package net.evilblock.cubed.statistic

interface ServerStatisticProvider
{
    fun ticksPerSecond(): Double
    fun cpuUsage(): Double
    fun memoryUsage(): Long
}
