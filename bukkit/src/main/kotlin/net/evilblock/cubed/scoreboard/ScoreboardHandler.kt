/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.scoreboard

import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ScoreboardHandler {

    internal var scoreboardProvider: ScoreboardAdapter = DefaultScoreboardProvider()
    internal val scoreboards: MutableMap<UUID, Scoreboard> = ConcurrentHashMap()

    internal lateinit var scoreboardOverride: ScoreboardOverride

    @JvmStatic
    fun configure(scoreboardProvider: ScoreboardAdapter) {
        this.scoreboardProvider = scoreboardProvider
    }

    class DefaultScoreboardProvider : ScoreboardAdapter() {
        override fun getTitle(player: Player): String {
            return "Un-configured scoreboard"
        }

        override fun getLines(board: LinkedList<String>, player: Player) {
            board.add("Hey there, ${player.name}!")
        }
    }
}