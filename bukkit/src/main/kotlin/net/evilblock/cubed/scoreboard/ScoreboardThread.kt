/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.scoreboard

import net.evilblock.cubed.CubedConfig

class ScoreboardThread : Thread("Cubed - Scoreboard") {

    init {
        this.isDaemon = true
    }

    override fun run() {
        while (true) {
            try {
                for (scoreboard in ScoreboardHandler.scoreboards.values) {
                    try {
                        if (System.currentTimeMillis() - scoreboard.createdAt >= 250L) {
                            scoreboard.update()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                sleep(CubedConfig.getScoreboardUpdateInterval() * 50L)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

}