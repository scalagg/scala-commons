package gg.scala.commons.tablist.cell;

import gg.scala.commons.tablist.util.Util;
import org.bukkit.entity.Player;

public enum CellStacking {

    HORIZONTAL,
    HORIZONTAL_FULL, 
    VERTICAL;

    public static CellStacking getVanillaStackingOfVersion(int protocolVersion) {
        return protocolVersion < 47 ? HORIZONTAL : VERTICAL;
    }

    public static CellStacking getVanillaStackingOf(Player holder) {
        return getVanillaStackingOfVersion(Util.getClientVersion(holder));
    }

}
