/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.scoreboard

import gg.scala.flavor.service.Close
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.Cubed

@Service
object ScoreboardThread : Thread("Cubed - Scoreboard") {

    init {
        this.isDaemon = true
    }

    @Configure
    fun configure()
    {
        this.isDaemon = true
        this.start()
    }

    @Close
    fun close()
    {
        stop()
    }

    override fun run() {
        while (true) {
            try {
                val provider = ScoreboardHandler
                    .scoreboardProvider ?: return

                for (scoreboard in ScoreboardHandler.scoreboards.values) {
                    try {
                        if (System.currentTimeMillis() - scoreboard.createdAt >= 250L) {
                            scoreboard.update()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                sleep(provider.getInterval() * 50L)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

}
