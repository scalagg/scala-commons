package gg.scala.commons.tablist.manage;

import gg.scala.commons.tablist.TablistHandle;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public interface TablistProvider {

    Collection<TablistHandle> getTablistHandles();

    TablistHandle getTablist(Player holder);

    void release(Player holder);

}
