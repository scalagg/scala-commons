package gg.scala.commons.tablist.manage;

import gg.scala.commons.tablist.TablistHandle;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public class DefaultTablistProvider implements TablistProvider {

    private final Map<UUID, TablistHandle> registry = new HashMap<>();

    @Override
    public Collection<TablistHandle> getTablistHandles() {
        return registry.values();
    }

    @Override
    public TablistHandle getTablist(Player holder) {
        TablistHandle handle = registry.get(holder.getUniqueId());

        if (handle == null) {
            registry.put(holder.getUniqueId(), handle = new TablistHandle(holder));
        }

        return handle;
    }

    @Override
    public void release(Player holder) {
        registry.remove(holder.getUniqueId());
    }

}
