package gg.scala.commons.tablist.adapter.populator;

import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public abstract class PlayersProvider {

    public abstract Collection<? extends Player> getPlayers(Player viewer);

}
