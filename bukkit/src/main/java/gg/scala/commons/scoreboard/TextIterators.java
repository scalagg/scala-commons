package gg.scala.commons.scoreboard;

import gg.scala.commons.scoreboard.animations.TextFadeAnimation;
import gg.scala.commons.scoreboard.animations.TextTypingAnimation;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public class TextIterators {

    public static TextIterator textTypingOldSchool(@NotNull String text) {
        return textTyping(text, "_", 10, 2);
    }

    public static TextIterator textTyping(@NotNull String text, @NotNull String cursor, int idleTicks, int typingSpeedTicks) {
        return new TextTypingAnimation(text, cursor, idleTicks, typingSpeedTicks);
    }

    public static TextIterator textFade(@NotNull String text, @NotNull ChatColor primaryColor, @NotNull ChatColor fadeColor, @NotNull ChatColor secondaryColor) {
        return new TextFadeAnimation(text, primaryColor, fadeColor, secondaryColor);
    }

    public static TextIterator textFadeHypixel(@NotNull String text) {
        return textFade(text, ChatColor.YELLOW, ChatColor.GOLD, ChatColor.WHITE);
    }
}
