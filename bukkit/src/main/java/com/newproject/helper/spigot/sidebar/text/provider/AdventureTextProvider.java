package com.newproject.helper.spigot.sidebar.text.provider;

import com.newproject.helper.spigot.sidebar.text.TextProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AdventureTextProvider implements TextProvider<Component> {

    public static final ComponentSerializer<Component, Component, String> GSON_SERIALIZER =
            GsonComponentSerializer.gson();
    public static final ComponentSerializer<Component, TextComponent, String> LEGACY_SERIALIZER =
            LegacyComponentSerializer.legacySection();


    @Override
    public String asJsonMessage(@NotNull Player player, @NotNull Component component) {
        return GSON_SERIALIZER.serialize(component);
    }

    @Override
    public Component emptyMessage() {
        return Component.empty();
    }

    @Override
    public Component fromLegacyMessage(@NotNull String message) {
        return LEGACY_SERIALIZER.deserialize(message);
    }

    @Override
    public String asLegacyMessage(@NotNull Player player, @NotNull Component component) {
        return LEGACY_SERIALIZER.serialize(component);
    }
}
