/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.scoreboard

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ScoreboardHandler
{
    internal val scoreboards = ConcurrentHashMap<UUID, Scoreboard>()

    var scoreboardOverride: ScoreboardOverride? = null
    internal var scoreboardProvider: ScoreboardAdapter? = null

    @JvmStatic
    fun configure(scoreboardProvider: ScoreboardAdapter)
    {
        this.scoreboardProvider = scoreboardProvider
    }

    init
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
                    board.add("Your location:")
                    board.add("${CC.GREEN}${
                        player.location.x.toInt()
                    }, ${player.location.y.toInt()}, ${
                        player.location.z.toInt()
                    }.")

                    board.add("")
                    board.add("${CC.GRAY}scala.gg")
                }
            }
        }
    }
}
