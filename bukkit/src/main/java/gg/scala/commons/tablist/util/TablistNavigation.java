package gg.scala.commons.tablist.util;

import gg.scala.commons.tablist.TablistHandle;

public class TablistNavigation {

    private static final int ENTRIES_PER_COLUMN = 20;

    public static int getIndex(TablistHandle handle, int x, int y) {
        return switch (handle.getCellStacking()) {
            case HORIZONTAL, HORIZONTAL_FULL -> (x * ENTRIES_PER_COLUMN) + y;
            case VERTICAL -> (y * ENTRIES_PER_COLUMN) + x;
        };
    }

}
