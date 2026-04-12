package club.frozed.tablist;

import io.github.nosequel.tab.shared.entry.TabElement;
import org.bukkit.entity.Player;

public interface TabAdapter {

    TabElement getLines(Player player);
}
