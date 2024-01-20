package gg.scala.commons.tablist.util;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public class ScoreboardTeamUtil {

    public static Object createTeam(String title, String prefix, String suffix, Collection<String> players,
                                    int propertySpec) {
        return PacketUtil.createTeamPacket(
                title, title,
                PacketUtil.TeamAction.CREATE,
                prefix, suffix,
                propertySpec, players,
                PacketUtil.TeamNametagVisibility.ALWAYS, 15
        );
    }

    public static Object deleteTeam(String title) {
        return PacketUtil.createTeamPacket(
                title, title,
                PacketUtil.TeamAction.DELETE,
                "", "",
                0, Collections.emptyList(),
                PacketUtil.TeamNametagVisibility.ALWAYS, 15
        );
    }

    public static Object updateTeam(String title, String prefix, String suffix, int propertySpec) {
        return PacketUtil.createTeamPacket(
                title, title,
                PacketUtil.TeamAction.UPDATE_INFO,
                prefix, suffix,
                propertySpec, Collections.emptyList(),
                PacketUtil.TeamNametagVisibility.ALWAYS, 15
        );
    }

    public static Object addPlayers(String title, Collection<String> players) {
        return PacketUtil.createTeamPacket(
                title, title,
                PacketUtil.TeamAction.ADD_PLAYERS,
                "", "",
                PacketUtil.TEAM_PROP_SPEC_NONE, players,
                PacketUtil.TeamNametagVisibility.ALWAYS, 15
        );
    }

    public static Object removePlayers(String title, Collection<String> players) {
        return PacketUtil.createTeamPacket(
                title, title,
                PacketUtil.TeamAction.REMOVE_PLAYERS,
                "", "",
                PacketUtil.TEAM_PROP_SPEC_NONE, players,
                PacketUtil.TeamNametagVisibility.ALWAYS, 15
        );
    }

}
