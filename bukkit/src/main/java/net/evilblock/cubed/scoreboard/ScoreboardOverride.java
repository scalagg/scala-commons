package net.evilblock.cubed.scoreboard;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.function.Function;

/**
 * @author puugz
 * @since 21/07/2021 19:22
 */
@Getter
@Setter
public abstract class ScoreboardOverride extends ScoreboardAdapter {

    public Function<Player, Boolean> shouldOverride;

}
