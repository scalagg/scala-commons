package gg.scala.commons.tablist.manage;

import gg.scala.commons.tablist.TablistHandle;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface TablistProvider {

    Collection<TablistHandle> getTablistHandles();

    TablistHandle getTablist(Player holder);

    void release(Player holder);

}
