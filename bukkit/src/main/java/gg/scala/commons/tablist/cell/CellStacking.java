package gg.scala.commons.tablist.cell;

import gg.scala.commons.tablist.util.Util;
import org.bukkit.entity.Player;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public enum CellStacking {

    HORIZONTAL,
    HORIZONTAL_FULL, // 1.8+ only
    VERTICAL;

    public static CellStacking getVanillaStackingOfVersion(int protocolVersion) {
        return protocolVersion < 47 ? HORIZONTAL : VERTICAL;
    }

    public static CellStacking getVanillaStackingOf(Player holder) {
        return getVanillaStackingOfVersion(Util.getClientVersion(holder));
    }

}
