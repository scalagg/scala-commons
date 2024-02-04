package gg.scala.commons.tablist.adapter.populator;

import gg.scala.commons.tablist.TablistPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public abstract class PlayersProvider {

    public abstract Collection<TablistPlayer> getPlayers(Player viewer);

}
