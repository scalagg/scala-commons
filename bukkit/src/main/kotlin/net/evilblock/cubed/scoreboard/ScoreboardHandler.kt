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

    internal var titleGetter: TitleGetter = DefaultTitleGetter()
    internal var scoreGetter: ScoreGetter = DefaultScoreGetter()

    internal val scoreboards: MutableMap<UUID, Scoreboard> = ConcurrentHashMap()

    internal lateinit var scoreboardOverride: ScoreboardOverride

    @JvmStatic
    fun configure(titleGetter: TitleGetter, scoreGetter: ScoreGetter) {
        this.titleGetter = titleGetter
        this.scoreGetter = scoreGetter
    }

    class DefaultTitleGetter : TitleGetter {
        override fun getTitle(player: Player): String {
            return "Un-configured scoreboard"
        }
    }

    class DefaultScoreGetter : ScoreGetter {
        override fun getScores(scores: LinkedList<String>, player: Player) {
            scores.add("Hey there, ${player.name}!")
        }
    }

}