package net.evilblock.cubed.scoreboard

import org.bukkit.entity.Player
import java.util.function.Function

/**
 * @author puugz
 * @since 09/07/2021 14:00
 */

open class ScoreboardOverride {

    internal var titleGetter: TitleGetter = ScoreboardHandler.DefaultTitleGetter()
    internal var scoreGetter: ScoreGetter = ScoreboardHandler.DefaultScoreGetter()

    internal lateinit var shouldOverride: Function<Player, Boolean>

}