package club.frozed.tablist;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class FrozedTablist {

    /*
    Forked from Hatsur API
    Links:
    -> https://github.com/norxir/seventab
    -> https://github.com/norxir/eighttab
     */

    private static FrozedTablist instance;
    private final TabAdapter adapter;

    public FrozedTablist(JavaPlugin plugin, TabAdapter adapter, int delay1, int delay2) {
        this.adapter = adapter;

//        ProtocolLibrary.getProtocolManager().addPacketListener(new TabPacket(plugin));

        Bukkit.getServer().getPluginManager().registerEvents(new TabListener(this), plugin);
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new TabRunnable(adapter), delay1, delay2);
    }

    public void deregisterPlayer(Player player) {
        TabLayout.getLayoutMapping().remove(player.getUniqueId());
    }
}
