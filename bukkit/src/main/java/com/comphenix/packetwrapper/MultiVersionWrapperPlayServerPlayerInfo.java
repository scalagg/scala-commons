package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.google.common.collect.Lists;
import net.evilblock.cubed.util.ServerVersion;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

public class MultiVersionWrapperPlayServerPlayerInfo extends AbstractPacket {
    public static final PacketType TYPE = Server.PLAYER_INFO;

    public MultiVersionWrapperPlayServerPlayerInfo() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public MultiVersionWrapperPlayServerPlayerInfo(PacketContainer packet) {
        super(packet, TYPE);
    }

    public @Nullable EnumWrappers.PlayerInfoAction getAction() {
        if (ServerVersion.getVersion().isOlderThanOrEquals(ServerVersion.v1_8_8)) {
            return this.handle.getPlayerInfoAction().read(0);
        }

        var actions = this.handle.getPlayerInfoActions().read(0);
        return actions.stream().findFirst().orElse(null);
    }

    public void setAction(EnumWrappers.PlayerInfoAction value) {
        if (ServerVersion.getVersion().isOlderThanOrEquals(ServerVersion.v1_8_8)) {
            this.handle.getPlayerInfoAction().write(0, value);
            return;
        }

        this.handle.getPlayerInfoActions().modify(0, previousValue -> new HashSet<>(List.of(value)));
    }

    public List<PlayerInfoData> getData() {
        return this.handle.getPlayerInfoDataLists().read(
            ServerVersion.getVersion().isOlderThanOrEquals(ServerVersion.v1_8_8) ? 0 : 1
        );
    }

    public void setData(List<PlayerInfoData> value) {
        if (ServerVersion.getVersion().isOlderThanOrEquals(ServerVersion.v1_8_8)) {
            this.handle.getPlayerInfoDataLists().write(0, value);
            return;
        }

        this.handle.getPlayerInfoDataLists().modify(1, previousValue -> value);
    }
}
