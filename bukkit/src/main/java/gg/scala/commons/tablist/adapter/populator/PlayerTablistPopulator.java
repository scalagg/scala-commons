package gg.scala.commons.tablist.adapter.populator;

import gg.scala.commons.tablist.TablistHandle;
import gg.scala.commons.tablist.adapter.TablistPopulator;
import gg.scala.commons.tablist.cell.TablistCell;
import gg.scala.commons.tablist.manage.TablistManager;
import gg.scala.commons.tablist.util.Util;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public class PlayerTablistPopulator extends TablistPopulator {

    private PlayersProvider provider;
    private final List<Integer> availableCells;

    private long nextListUpdate;
    private boolean requiresUpdate = true;
    private int previousEntryCount;

    public PlayerTablistPopulator(TablistHandle tablist, PlayersProvider provider) {
        super(tablist);
        this.provider = provider;
        this.availableCells = TablistCellMapping.getAllCells(tablist.extendedFeaturesEnabled() ? 47 : 5);
    }

    public PlayerTablistPopulator(TablistHandle tablist, PlayersProvider provider, List<Integer> availableCells) {
        super(tablist);
        this.provider = provider;
        this.availableCells = availableCells;
    }

    public PlayersProvider getProvider() {
        return provider;
    }

    public List<Integer> getAvailableCells() {
        return availableCells;
    }

    public void setProvider(PlayersProvider provider) {
        this.provider = provider == null ? DefaultPlayersProvider.getInstance() : provider;
    }

    public void requireUpdate() {
        requiresUpdate = true;
    }

    protected boolean canUpdate() {
        if (!requiresUpdate || availableCells.isEmpty()) {
            return false;
        }

        return System.currentTimeMillis() >= nextListUpdate;
    }

    public void onUpdate() {
        requiresUpdate = false;
        nextListUpdate = System.currentTimeMillis() + RandomUtils.nextLong(4000, 6000);

        Collection<? extends Player> users = provider.getPlayers(parent.getHolder());
        int actualVersion = Util.getClientVersion(parent.getHolder());

        int i = 0;
        for (Player other : users) {
            int nextIndex = availableCells.get(i++);

            TablistCell entry = parent.getCell(nextIndex);

            TablistHandle handle = TablistManager.get().getTablist(other);
            String appearance = handle.getTablistAppearance(actualVersion);

            if (appearance == null) {
                appearance = other.getName();
            }

            entry.setValue(appearance);

            parent.updateCell(nextIndex);

            if (i == availableCells.size()) {
                break;
            }
        }

        if (users.size() < availableCells.size() && users.size() < previousEntryCount) {
            for (i = users.size(); i < previousEntryCount; i++) {
                int nextIndex = availableCells.get(i);

                TablistCell entry = parent.getCell(nextIndex);
                entry.setValue(null);
                parent.updateCell(nextIndex);
            }
        }

        previousEntryCount = i;
    }

}
