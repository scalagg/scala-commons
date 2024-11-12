package com.newproject.helper.spigot.sidebar.text.provider;

import com.newproject.helper.spigot.sidebar.text.TextProvider;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MiniMessageTextProvider implements TextProvider<String> {

    protected final MiniMessage miniMessage;

    public MiniMessageTextProvider(MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    @Override
    public String asJsonMessage(@NotNull Player player, @NotNull String message) {
        return AdventureTextProvider.GSON_SERIALIZER.serialize(miniMessage.deserialize(message));
    }

    @Override
    public String emptyMessage() {
        return "";
    }

    @Override
    public String asLegacyMessage(@NotNull Player player, @NotNull String component) {
        return AdventureTextProvider.LEGACY_SERIALIZER.serialize(miniMessage.deserialize(component));
    }

    @Override
    public String fromLegacyMessage(@NotNull String message) {
        return message; // will be transformed on packet level
    }
}
