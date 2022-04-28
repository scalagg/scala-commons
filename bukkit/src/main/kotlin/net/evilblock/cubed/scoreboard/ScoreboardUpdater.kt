/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.scoreboard

import net.evilblock.cubed.Cubed
import java.util.logging.Level

object ScoreboardUpdater : Runnable
{
    override fun run()
    {
        val provider = ScoreboardHandler
            .scoreboardProvider

        if (provider == null)
        {
            Cubed.instance.logger.info("No scoreboard provider was registered!")
            return
        }

        for (scoreboard in ScoreboardHandler.scoreboards.values)
        {
            kotlin.runCatching {
                scoreboard.update()
            }.onFailure {
                Cubed.instance.logger.log(
                    Level.WARNING, "Exception during scoreboard tick", it
                )
            }
        }
    }
}
