package gg.scala.commons.tablist.adapter;

import gg.scala.commons.tablist.TablistHandle;

public abstract class TablistPopulator {

    protected final TablistHandle parent;

    public TablistPopulator(TablistHandle parent) {
        this.parent = parent;
    }

    public final void onTick() {
        if (canUpdate()) {
            onUpdate();
        }
    }

    protected abstract boolean canUpdate();

    protected abstract void onUpdate();

}
