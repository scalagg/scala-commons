/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.logging

import net.evilblock.cubed.scheduler.AsyncHourMarkEvent
import net.evilblock.cubed.scheduler.CubedScheduler
import net.evilblock.cubed.util.bukkit.Tasks
import java.util.concurrent.ConcurrentHashMap

object LogHandler
{

    private var logFiles: MutableSet<LogFile> = ConcurrentHashMap.newKeySet()

    fun initialLoad()
    {
        CubedScheduler.trackScheduledTask<AsyncHourMarkEvent> { completeFlush() }
    }

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
