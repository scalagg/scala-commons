package club.frozed.tablist;

import io.github.nosequel.tab.shared.entry.TabElement;
import org.bukkit.entity.Player;

public interface TabAdapter {

    /**
     * Get the tab lines for a player.
     *
     * @param player the player
     * @return map
     */
    TabElement getLines(Player player);
}
