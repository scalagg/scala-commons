package net.evilblock.cubed.scoreboard

import org.bukkit.entity.Player

/**
 * @author puugz
 * @since 23/08/2021 22:01
 */
abstract class ScoreboardOverride : ScoreboardAdapter() {

    abstract fun shouldOverride(player: Player): Boolean

}