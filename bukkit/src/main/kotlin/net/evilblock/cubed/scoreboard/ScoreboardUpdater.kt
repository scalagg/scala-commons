/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.scoreboard

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service
import net.evilblock.cubed.ScalaCommonsSpigot
import java.util.logging.Level

@Service
object ScoreboardUpdater : Runnable
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    override fun run()
    {
        val provider = ScoreboardHandler
            .scoreboardProvider

        if (provider == null)
        {
            plugin.logger.info("No scoreboard provider was registered!")
            return
        }

        for (scoreboard in ScoreboardHandler.scoreboards.values)
        {
            kotlin.runCatching {
                scoreboard.update()
            }.onFailure {
                plugin.logger.log(
                    Level.WARNING, "Exception during scoreboard tick", it
                )
            }
        }
    }
}
