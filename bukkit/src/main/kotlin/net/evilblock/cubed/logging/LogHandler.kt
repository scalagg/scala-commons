/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.logging

import gg.scala.flavor.service.Close
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.scheduler.AsyncHourMarkEvent
import net.evilblock.cubed.scheduler.CubedScheduler
import net.evilblock.cubed.util.bukkit.Tasks
import java.util.concurrent.ConcurrentHashMap

@Service
object LogHandler
{
    private var logFiles: MutableSet<LogFile> =
        ConcurrentHashMap.newKeySet()

    @Configure
    fun configure()
    {
        CubedScheduler
            .trackScheduledTask<AsyncHourMarkEvent> {
                completeFlush()
            }
    }

    @Close
    fun completeFlush()
    {
        for (logFile in logFiles)
        {
            logFile.flush()
        }
    }

    fun trackLogFile(logFile: LogFile)
    {
        logFiles.add(logFile)
    }

    fun forgetLogFile(logFile: LogFile)
    {
        Tasks.async {
            logFile.flush()
        }

        logFiles.remove(logFile)
    }

}
