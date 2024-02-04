package gg.scala.commons.tablist.util;

import gg.scala.commons.tablist.util.data.Profile;
import gg.scala.commons.tablist.util.data.TablistEntry;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public class TablistEntryHelper {

    public static TablistEntry.Builder forPlayer(Player player) {
        Profile profile = Util.getPlayerProfile(player);
        
        return TablistEntry.builder().userId(profile.getId())
                .name(profile.getName())
                .icon(profile.getSkinData())
                .gameMode(player.getGameMode());
    }

    public static TablistEntry.Builder of(Profile profile) {
        return TablistEntry.builder().userId(profile.getId())
                .name(profile.getName())
                .icon(profile.getSkinData())
                .gameMode(GameMode.SURVIVAL);
    }

}
