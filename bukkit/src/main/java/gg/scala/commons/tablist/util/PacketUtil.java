package gg.scala.commons.tablist.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import gg.scala.commons.tablist.util.data.SkinData;
import gg.scala.commons.tablist.util.data.TablistEntry;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.WorldSettings;
import org.bukkit.GameMode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PacketUtil {

    private static Field PPO_PLAYER_INFO_ACTION_FIELD;
    private static Field PPO_PLAYER_INFO_ENTRIES_FIELD;
    private static Constructor PPO_PLAYER_INFO_ENTRY_CONSTRUCTOR;
    private static boolean USE_STATIC_ENTRY_CONSTRUCTOR = false;

    private static Field PPO_SCOREBOARD_TEAM_TEAM_NAME_FIELD;
    private static Field PPO_SCOREBOARD_TEAM_TEAM_TEAM_DISPLAY_NAME_FIELD;
    private static Field PPO_SCOREBOARD_TEAM_TEAM_TEAM_PREFIX_FIELD;
    private static Field PPO_SCOREBOARD_TEAM_TEAM_TEAM_SUFFIX_FIELD;
    private static Field PPO_SCOREBOARD_TEAM_TEAM_NAME_VISIBILITY_FIELD;
    private static Field PPO_SCOREBOARD_TEAM_TEAM_COLOR_FIELD;
    private static Field PPO_SCOREBOARD_TEAM_TEAM_ENTRIES_FIELD;
    private static Field PPO_SCOREBOARD_TEAM_ACTION_FIELD;
    private static Field PPO_SCOREBOARD_TEAM_TEAM_PROPERTY_SPEC_FIELD;

    static {
        try {
            PPO_PLAYER_INFO_ACTION_FIELD = PacketPlayOutPlayerInfo.class.getDeclaredField("a");
            PPO_PLAYER_INFO_ACTION_FIELD.setAccessible(true);
            PPO_PLAYER_INFO_ENTRIES_FIELD = PacketPlayOutPlayerInfo.class.getDeclaredField("b");
            PPO_PLAYER_INFO_ENTRIES_FIELD.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            PPO_PLAYER_INFO_ENTRY_CONSTRUCTOR = PacketPlayOutPlayerInfo.PlayerInfoData.class.getConstructor(
                    PacketPlayOutPlayerInfo.class, GameProfile.class, int.class, WorldSettings.EnumGamemode.class,
                    IChatBaseComponent.class);
        } catch (Throwable ignored) {
            USE_STATIC_ENTRY_CONSTRUCTOR = true;

            try {
                PPO_PLAYER_INFO_ENTRY_CONSTRUCTOR = PacketPlayOutPlayerInfo.PlayerInfoData.class.getConstructor(
                        GameProfile.class, int.class, WorldSettings.EnumGamemode.class, IChatBaseComponent.class);
            } catch (Throwable t0) {
                t0.printStackTrace();
            }
        }

        try {

            PPO_SCOREBOARD_TEAM_TEAM_NAME_FIELD = PacketPlayOutScoreboardTeam.class.getDeclaredField("a");
            PPO_SCOREBOARD_TEAM_TEAM_NAME_FIELD.setAccessible(true);
            PPO_SCOREBOARD_TEAM_TEAM_TEAM_DISPLAY_NAME_FIELD = PacketPlayOutScoreboardTeam.class.getDeclaredField("b");
            PPO_SCOREBOARD_TEAM_TEAM_TEAM_DISPLAY_NAME_FIELD.setAccessible(true);
            PPO_SCOREBOARD_TEAM_TEAM_TEAM_PREFIX_FIELD = PacketPlayOutScoreboardTeam.class.getDeclaredField("c");
            PPO_SCOREBOARD_TEAM_TEAM_TEAM_PREFIX_FIELD.setAccessible(true);
            PPO_SCOREBOARD_TEAM_TEAM_TEAM_SUFFIX_FIELD = PacketPlayOutScoreboardTeam.class.getDeclaredField("d");
            PPO_SCOREBOARD_TEAM_TEAM_TEAM_SUFFIX_FIELD.setAccessible(true);
            PPO_SCOREBOARD_TEAM_TEAM_NAME_VISIBILITY_FIELD = PacketPlayOutScoreboardTeam.class.getDeclaredField("e");
            PPO_SCOREBOARD_TEAM_TEAM_NAME_VISIBILITY_FIELD.setAccessible(true);
            PPO_SCOREBOARD_TEAM_TEAM_COLOR_FIELD = PacketPlayOutScoreboardTeam.class.getDeclaredField("f");
            PPO_SCOREBOARD_TEAM_TEAM_COLOR_FIELD.setAccessible(true);
            PPO_SCOREBOARD_TEAM_TEAM_ENTRIES_FIELD = PacketPlayOutScoreboardTeam.class.getDeclaredField("g");
            PPO_SCOREBOARD_TEAM_TEAM_ENTRIES_FIELD.setAccessible(true);
            PPO_SCOREBOARD_TEAM_ACTION_FIELD = PacketPlayOutScoreboardTeam.class.getDeclaredField("h");
            PPO_SCOREBOARD_TEAM_ACTION_FIELD.setAccessible(true);
            PPO_SCOREBOARD_TEAM_TEAM_PROPERTY_SPEC_FIELD = PacketPlayOutScoreboardTeam.class.getDeclaredField("i");
            PPO_SCOREBOARD_TEAM_TEAM_PROPERTY_SPEC_FIELD.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public enum TablistPacketAction {
        ADD_PLAYER,
        UPDATE_GAMEMODE,
        UPDATE_PING,
        UPDATE_DISPLAY_NAME,
        REMOVE_PLAYER
    }

    public static Object createTablistPacket(TablistPacketAction action, TablistEntry entry) {
        List<TablistEntry> list = new ArrayList<>(1);
        list.add(entry);
        return createTablistPacket(action, list);
    }

    public static Object createTablistPacket(TablistPacketAction action, Collection<TablistEntry> entries) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();

        try {
            PPO_PLAYER_INFO_ACTION_FIELD.set(packet, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.values()[action.ordinal()]);
            PPO_PLAYER_INFO_ENTRIES_FIELD.set(packet, entries.stream()
                    .map(entry -> {
                        GameProfile nmsProfile = new GameProfile(entry.getUserId(), entry.getName());
                        SkinData icon = entry.getIcon();

                        if (icon != null) {
                            nmsProfile.getProperties().put("textures", new Property("textures", icon.getValue(), icon.getSignature()));
                        }

                        GameMode mode = entry.getGameMode() == null ? GameMode.SURVIVAL : entry.getGameMode();
                        net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode nmsMode = net.minecraft.server.v1_8_R3.WorldSettings.a(mode.ordinal());

                        Object instance = null;

                        try {
                            instance = USE_STATIC_ENTRY_CONSTRUCTOR ?
                                    PPO_PLAYER_INFO_ENTRY_CONSTRUCTOR.newInstance(
                                            nmsProfile,
                                            entry.getPing(),
                                            nmsMode,
                                            IChatBaseComponent.ChatSerializer.a(entry.hasDisplayName() ? entry.getDisplayName() : entry.getName())) :
                                    PPO_PLAYER_INFO_ENTRY_CONSTRUCTOR.newInstance(
                                            packet,
                                            nmsProfile,
                                            entry.getPing(),
                                            nmsMode,
                                            IChatBaseComponent.ChatSerializer.a(entry.hasDisplayName() ? entry.getDisplayName() : entry.getName()));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        return instance;
                    }).collect(Collectors.toList()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return packet;
    }

    public enum TeamAction {
        CREATE,
        DELETE,
        UPDATE_INFO,
        ADD_PLAYERS,
        REMOVE_PLAYERS
    }

    public enum TeamNametagVisibility {
        ALWAYS("always"),
        NEVER("never"),
        HIDE_FOR_OTHER_TEAMS("hideForOtherTeams"),
        HIDE_FOR_OWN_TEAM("hideForOwnTeam");

        final String id;

        TeamNametagVisibility(String id) {
            this.id = id;
        }
    }

    public static final int TEAM_PROP_SPEC_NONE = 0x00;
    public static final int TEAM_PROP_SPEC_ALLOW_FRIENDLY_FIRE = 0x01;
    public static final int TEAM_PROP_SPEC_SEE_FRIENDLY_INVISIBLE = 0x02;

    public static int createTeamPropertySpec(boolean allowFriendlyFire, boolean canSeeFriendlyInvisible) {
        int value = TEAM_PROP_SPEC_NONE;

        if (allowFriendlyFire) {
            value |= TEAM_PROP_SPEC_ALLOW_FRIENDLY_FIRE;
        }

        if (canSeeFriendlyInvisible) {
            value |= TEAM_PROP_SPEC_SEE_FRIENDLY_INVISIBLE;
        }

        return value;
    }

    public static Object createTeamPacket(String name, String displayName, TeamAction action, String prefix, String suffix,
                                          int propertySpec, Collection<String> entries,
                                          TeamNametagVisibility nameVisibility, int color) {
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();

        try {
            PPO_SCOREBOARD_TEAM_TEAM_NAME_FIELD.set(packet, name);
            PPO_SCOREBOARD_TEAM_TEAM_TEAM_DISPLAY_NAME_FIELD.set(packet, displayName);
            PPO_SCOREBOARD_TEAM_TEAM_TEAM_PREFIX_FIELD.set(packet, prefix);
            PPO_SCOREBOARD_TEAM_TEAM_TEAM_SUFFIX_FIELD.set(packet, suffix);
            PPO_SCOREBOARD_TEAM_TEAM_NAME_VISIBILITY_FIELD.set(packet, nameVisibility.id);
            PPO_SCOREBOARD_TEAM_TEAM_COLOR_FIELD.set(packet, color);
            PPO_SCOREBOARD_TEAM_TEAM_ENTRIES_FIELD.set(packet, entries);
            PPO_SCOREBOARD_TEAM_ACTION_FIELD.set(packet, action.ordinal());
            PPO_SCOREBOARD_TEAM_TEAM_PROPERTY_SPEC_FIELD.set(packet, propertySpec);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return packet;
    }

}
