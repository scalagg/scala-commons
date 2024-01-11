package gg.scala.commons.scoreboard;

import gg.scala.commons.scoreboard.animations.TextFadeAnimation;
import gg.scala.commons.scoreboard.animations.TextTypingAnimation;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public class TextIterators {

    /**
     * Creates new text typing animation.
     *
     * @param text - text to type
     * @return new text typing animation
     */
    public static TextIterator textTypingOldSchool(@NotNull String text) {
        return textTyping(text, "_", 10, 2);
    }

    /**
     * Creates new text typing animation.
     *
     * @param text             - text to type
     * @param cursor           - cursor to flick
     * @param idleTicks        - idle ticks between typing
     * @param typingSpeedTicks - ticks between each character
     * @return new text typing animation
     */
    public static TextIterator textTyping(@NotNull String text, @NotNull String cursor, int idleTicks, int typingSpeedTicks) {
        return new TextTypingAnimation(text, cursor, idleTicks, typingSpeedTicks);
    }

    /**
     * Creates new text fade animation.
     *
     * @param text           - text to fade
     * @param primaryColor   - primary color
     * @param fadeColor      - fade color
     * @param secondaryColor - secondary color
     * @return new text fade animation
     */
    public static TextIterator textFade(@NotNull String text, @NotNull ChatColor primaryColor, @NotNull ChatColor fadeColor, @NotNull ChatColor secondaryColor) {
        return new TextFadeAnimation(text, primaryColor, fadeColor, secondaryColor);
    }

    /**
     * Creates new text fade animation. Just like on Hypixel server sidebars title.
     *
     * @param text - text to fade
     * @return new text fade animation
     */
    public static TextIterator textFadeHypixel(@NotNull String text) {
        return textFade(text, ChatColor.YELLOW, ChatColor.GOLD, ChatColor.WHITE);
    }
}
