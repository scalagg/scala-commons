package net.evilblock.cubed.scoreboard

import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard
import java.util.*

/**
 * @author puugz
 * @since 23/08/2021 21:58
 */
abstract class ScoreboardAdapter {

    abstract fun getTitle(player: Player): String

    abstract fun getLines(board: LinkedList<String>, player: Player)

    fun getInterval(): Long {
        return 3L
    }

    fun onScoreboardCreate(player: Player, scoreboard: Scoreboard) {

    }

    fun preRun(player: Player, scoreboard: Scoreboard) {

    }
}