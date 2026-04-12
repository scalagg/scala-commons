package net.evilblock.cubed.scoreboard

import org.bukkit.entity.Player

abstract class ScoreboardOverride : ScoreboardAdapter() {

    abstract fun shouldOverride(player: Player): Boolean

}