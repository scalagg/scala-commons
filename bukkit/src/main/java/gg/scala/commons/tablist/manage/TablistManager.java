package gg.scala.commons.tablist.manage;

import gg.scala.commons.tablist.TablistHandle;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public class TablistManager {

    private static TablistManager INSTANCE;

    public static TablistManager init() {
        return init(new DefaultTablistProvider());
    }

    public static TablistManager init(TablistProvider provider) {
        if (INSTANCE != null) {
            throw new IllegalStateException("Tablist manager already initialized");
        }

        return INSTANCE = new TablistManager(provider);
    }

    public static TablistManager get() {
        if (INSTANCE == null) {
            INSTANCE = init();
        }

        return INSTANCE;
    }

    public static Optional<TablistManager> getIfInitialized() {
        return Optional.ofNullable(INSTANCE);
    }

    private final TablistProvider provider;

    private TablistManager(TablistProvider provider) {
        this.provider = provider;
    }

    public Collection<TablistHandle> getTablistHandles() {
        return provider.getTablistHandles();
    }

    public TablistHandle getTablist(Player player) {
        return provider.getTablist(player);
    }

    public void release(Player player) {
        provider.release(player);
    }

}
