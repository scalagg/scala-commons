package club.frozed.tablist;

import club.frozed.tablist.skin.Skin;
import io.github.nosequel.tab.shared.entry.TabElement;
import io.github.nosequel.tab.shared.entry.TabEntry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by Ryzeon
 * Project: TabAPI
 * Date: 11/12/2020 @ 10:08
 */

public class TabRunnable implements Runnable {
    private final TabAdapter adapter;

    public TabRunnable(TabAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (TabLayout.getLayoutMapping().containsKey(player.getUniqueId())) {
                TabLayout layout = TabLayout.getLayoutMapping().get(player.getUniqueId());
                final TabElement tabEntryList = adapter.getLines(player);
                if (tabEntryList ==  null) {
                    continue;
                }

/*                for (TabEntry entry : tabEntryList.getEntries()) {
                    layout.update(entry.getX(), entry.getY(), entry.getText(), entry.getPing(), new Skin(entry.getSkinData()[0], entry.getSkinData()[1]));
                }*/



                for (int row = 0; row < 20; row++) {
                    for (int column = 0; column < 3; column++) {
                        layout.update(column, row, column + ", " + row, TabLatency.NO_BAR.getValue(), Skin.DEFAULT_SKIN);
                    }
                }
            } else {

                System.out.println("Skipping cuz layout mapping null");
            }
        }
    }
}
