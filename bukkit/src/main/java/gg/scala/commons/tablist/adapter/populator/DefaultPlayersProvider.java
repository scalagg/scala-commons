package gg.scala.commons.tablist.adapter.populator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public final class DefaultPlayersProvider extends PlayersProvider {

    private static final DefaultPlayersProvider INSTANCE = new DefaultPlayersProvider();

    public static DefaultPlayersProvider getInstance() {
        return INSTANCE;
    }

    private DefaultPlayersProvider() { }

    @Override
    public Collection<? extends Player> getPlayers(Player viewer) {
        return Bukkit.getServer().getOnlinePlayers().stream().filter(viewer::canSee).collect(Collectors.toList());
    }

}
