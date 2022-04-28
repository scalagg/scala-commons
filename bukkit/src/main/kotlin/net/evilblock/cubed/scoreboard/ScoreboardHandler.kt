/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.scoreboard

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Service
object ScoreboardHandler
{
    internal val scoreboards = mutableMapOf<UUID, Scoreboard>()

    var scoreboardOverride: ScoreboardOverride? = null
    internal var scoreboardProvider: ScoreboardAdapter? = null

    private val executor = Executors
        .newSingleThreadScheduledExecutor()

    private var executorEnabled = false

    @Configure
    fun configure()
    {
        val cubed = Cubed.instance

        if (
            cubed.config
                .getBoolean("scoreboard.default")
        )
        {
            scoreboardProvider = object : ScoreboardAdapter()
            {
                override fun getTitle(player: Player) =
                    "${CC.B_GOLD}scala.gg"

                override fun getLines(
                    board: LinkedList<String>, player: Player
                )
                {
                    board.add(CC.SB_BAR)
                    board.add("Your location:")
                    board.add("${CC.GREEN}${
                        player.location.x.toInt()
                    }, ${player.location.y.toInt()}, ${
                        player.location.z.toInt()
                    }.")

                    board.add("")
                    board.add("${CC.GREEN}scala.gg")
                    board.add(CC.SB_BAR)
                }
            }
        }
    }

    @JvmStatic
    fun configure(provider: ScoreboardAdapter)
    {
        this.scoreboardProvider = provider

        if (!this.executorEnabled)
        {
            this.executorEnabled = true
        } else
        {
            this.executor.shutdownNow()
        }

        this.executor.scheduleAtFixedRate(
            ScoreboardUpdater, 0L,
            provider.getInterval() * 50L,
            TimeUnit.MILLISECONDS
        )
    }
}
