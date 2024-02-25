package club.frozed.tablist;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class TabListener implements Listener {

    private final FrozedTablist instance;

    public TabListener(FrozedTablist instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        instance.deregisterPlayer(player);
    }
}
