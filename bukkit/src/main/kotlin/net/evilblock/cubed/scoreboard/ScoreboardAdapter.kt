package net.evilblock.cubed.scoreboard

import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard
import java.util.*

abstract class ScoreboardAdapter {

    abstract fun getTitle(player: Player): String

    abstract fun getLines(board: LinkedList<String>, player: Player)

    open fun getInterval(): Long {
        return 3L
    }

    open fun onScoreboardCreate(player: Player, scoreboard: Scoreboard) {

    }

    open fun preRun(player: Player, scoreboard: Scoreboard) {

    }
}