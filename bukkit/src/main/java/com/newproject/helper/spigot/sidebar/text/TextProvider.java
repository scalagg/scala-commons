package com.newproject.helper.spigot.sidebar.text;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface TextProvider<T> {

    String asJsonMessage(@NotNull Player player, @NotNull T component);

    String asLegacyMessage(@NotNull Player player, @NotNull T component);

    T emptyMessage();

    T fromLegacyMessage(@NotNull String message);
}
