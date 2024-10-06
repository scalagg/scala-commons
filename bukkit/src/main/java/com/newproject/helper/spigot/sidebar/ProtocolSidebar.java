package com.newproject.helper.spigot.sidebar;

import com.newproject.helper.spigot.sidebar.text.TextIterator;
import com.newproject.helper.spigot.sidebar.text.TextProvider;
import com.newproject.helper.spigot.sidebar.text.provider.AdventureTextProvider;
import com.newproject.helper.spigot.sidebar.text.provider.MiniMessageTextProvider;
import me.lucko.helper.Helper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

/**
 * Entry point class for creating new sidebars.
 * <p>
 * This class provides methods for creating new sidebars with different text providers.
 * <p>
 *
 * @author CatCoder
 */
public final class ProtocolSidebar {

    private ProtocolSidebar() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Creates new sidebar with custom text provider.
     *
     * @param title        - sidebar title
     * @param textProvider - text provider
     * @param <R>          - component entity type
     * @return new sidebar
     */
    public static <R> Sidebar<R> newSidebar(
            @NotNull R title,
            @NotNull TextProvider<R> textProvider
    ) {
        return new Sidebar<>(title, Helper.hostPlugin(), textProvider);
    }

    /**
     * {@inheritDoc}
     */
    public static <R> Sidebar<R> newSidebar(
            @NotNull TextIterator title,
            @NotNull TextProvider<R> textProvider
    ) {
        return new Sidebar<>(title, Helper.hostPlugin(), textProvider);
    }


    /**
     * Creates new sidebar with {@see https://docs.advntr.dev/minimessage/api.html MiniMessage} text provider.
     * Adventure are natively supported on Paper 1.16.5+.
     *
     * @param title       - sidebar title
     * @param miniMessage - MiniMessage instance
     * @return new sidebar
     */
    public static Sidebar<String> newMiniMessageSidebar(
            @NotNull String title,
            @NotNull MiniMessage miniMessage
    ) {
        return newSidebar(title, new MiniMessageTextProvider(miniMessage));
    }

    /**
     * {@inheritDoc}
     */
    public static Sidebar<String> newMiniMessageSidebar(
            @NotNull TextIterator title,
            @NotNull MiniMessage miniMessage
    ) {
        return newSidebar(title, new MiniMessageTextProvider(miniMessage));
    }

    /**
     * {@inheritDoc}
     */
    public static Sidebar<String> newMiniMessageSidebar(
            @NotNull String title
    ) {
        return newSidebar(title, new MiniMessageTextProvider(MiniMessage.miniMessage()));
    }

    /**
     * {@inheritDoc}
     */
    public static Sidebar<String> newMiniMessageSidebar(
            @NotNull TextIterator title
    ) {
        return newSidebar(title, new MiniMessageTextProvider(MiniMessage.miniMessage()));
    }

    /**
     * Creates new sidebar with {@see https://docs.advntr.dev/getting-started.html Adventure} text provider.
     * Adventure are natively supported on Paper 1.16.5+.
     *
     * @param title  - sidebar title
     * @return new sidebar
     */
    public static Sidebar<Component> newAdventureSidebar(
            @NotNull Component title
    ) {
        return newSidebar(title, new AdventureTextProvider());
    }

    /**
     * {@inheritDoc}
     */
    public static Sidebar<Component> newAdventureSidebar(
            @NotNull TextIterator title
    ) {
        return newSidebar(title, new AdventureTextProvider());
    }
}
