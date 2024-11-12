package com.newproject.helper.spigot.sidebar.util.version;

import com.viaversion.viaversion.api.Via;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class VersionUtil {

    public static final int SERVER_VERSION = MinecraftProtocolVersion.getCurrentVersion();

    public static int getPlayerVersion(@NotNull UUID id) {
        boolean isVia = Bukkit.getPluginManager().isPluginEnabled("ViaVersion");
        return isVia ? Via.getAPI().getPlayerVersion(id) : SERVER_VERSION;
    }
}
