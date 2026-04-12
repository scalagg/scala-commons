package gg.scala.commons.tablist.adapter.populator;

import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import gg.scala.commons.tablist.TablistPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

public final class DefaultPlayersProvider extends PlayersProvider {

    private static final DefaultPlayersProvider INSTANCE = new DefaultPlayersProvider();

    public static DefaultPlayersProvider getInstance() {
        return INSTANCE;
    }

    private DefaultPlayersProvider() { }

    @Override
    public Collection<TablistPlayer> getPlayers(Player viewer) {
        return Bukkit.getServer().getOnlinePlayers().stream().filter(viewer::canSee)
            .map(player -> new TablistPlayer(
                "",
                "",
                1,
                1,
                player.getName(),
                player.getDisplayName()
            ))
            .collect(Collectors.toList());
    }

}
