package gg.scala.commons.tablist.adapter;

import gg.scala.commons.tablist.TablistHandle;
import gg.scala.commons.tablist.util.Util;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public abstract class TablistAdapter {

    protected final TablistHandle handle;

    public TablistAdapter(TablistHandle handle) {
        this.handle = handle;
    }

    public abstract void initTablist();

    public abstract void createTablist();

    public abstract void destroyTablist();

    protected void sendPacketToHolder(Object nmsPacket) {
        Util.sendPacket(handle.getHolder(), nmsPacket);
    }

}
