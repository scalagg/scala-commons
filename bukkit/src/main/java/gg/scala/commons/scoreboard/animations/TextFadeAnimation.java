package gg.scala.commons.scoreboard.animations;

import com.google.common.base.Preconditions;
import gg.scala.commons.scoreboard.FrameIterator;
import gg.scala.commons.scoreboard.TextFrame;
import gg.scala.commons.scoreboard.TextIterator;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TextFadeAnimation extends TextIterator {

    private final String text;
    private final ChatColor primaryColor;
    private final ChatColor fadeColor;
    private final ChatColor secondaryColor;

    private final String colorCodes;

    private final FrameIterator frameIterator;

    public TextFadeAnimation(
            @NotNull String text,
            @NotNull ChatColor primaryColor,
            @NotNull ChatColor fadeColor,
            @NotNull ChatColor secondaryColor) {
        Preconditions.checkArgument(text.length() >= 3, "Text length must be at least 3 characters");

        this.text = ChatColor.stripColor(text);
        this.primaryColor = primaryColor;
        this.fadeColor = fadeColor;
        this.secondaryColor = secondaryColor;
        this.colorCodes = ChatColor.getLastColors(text);

        this.frameIterator = new FrameIterator(createAnimationFrames());

    }

    @Override
    public FrameIterator getFrameIterator() {
        return frameIterator;
    }

    @Override
    protected void start(List<TextFrame> frames) {
        frames.add(TextFrame.of(primaryColor + colorCodes + text, 3 * 20));
    }

    @Override
    public String next() {
        return frameIterator.next();
    }

    @Override
    public void forEachRemaining(Consumer<? super String> action) {
        frameIterator.forEachRemaining(action);
    }

    @Override
    public void remove() {
        frameIterator.remove();
    }

    @Override
    protected void end(List<TextFrame> frames) {

        TextFrame secondary = TextFrame.of(secondaryColor + colorCodes + text, 8);
        TextFrame primary = TextFrame.of(primaryColor + colorCodes + text, 8);

        frames.add(secondary);
        frames.add(primary);
        frames.add(secondary);
        frames.add(primary);
    }

    public List<TextFrame> createAnimationFrames() {
        List<TextFrame> frames = new ArrayList<>();

        start(frames);

        int primaryColorLength = primaryColor.toString().length();

        for (int i = 0; i < text.length(); i++) {
            StringBuilder builder = new StringBuilder(text);

            int offset = 0;

            if (i > 0) {
                builder.insert(0, secondaryColor);

                offset += colorCodes.length();
                builder.insert(offset, colorCodes);

                offset += primaryColorLength;
            }

            builder.insert(i + offset, colorCodes);
            builder.insert(i + 1 + offset + colorCodes.length(), primaryColor);

            builder.insert(i + offset + primaryColorLength + colorCodes.length() + 1, colorCodes);
            builder.insert(i + offset, fadeColor);

            if (i + 1 == text.length()) {

                builder.setLength(builder.length() - primaryColorLength - colorCodes.length());
            }

            frames.add(TextFrame.of(builder.toString(), 2));

        }

        end(frames);

        return frames;
    }

}
