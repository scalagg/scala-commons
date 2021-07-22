package net.evilblock.cubed.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.LinkedList;

/**
 * @author puugz
 * @since 21/07/2021 19:13
 */
public abstract class ScoreboardAdapter {

    public abstract String getTitle(Player player);

    public abstract void getLines(LinkedList<String> board, Player player);

    public long getInterval() {
        return 3L;
    }

    public void onScoreboardCreate(Player player, Scoreboard scoreboard) {

    }

    public void preRun(Player player, Scoreboard scoreboard) {

    }
}
