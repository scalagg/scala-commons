package gg.scala.commons.scoreboard.animations;

import gg.scala.commons.scoreboard.FrameIterator;
import gg.scala.commons.scoreboard.TextFrame;
import gg.scala.commons.scoreboard.TextIterator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TextTypingAnimation extends TextIterator {

    private final String text;
    private final String cursor;
    private final int idleTimeTicks;
    private final int typingSpeed;

    private final FrameIterator frameIterator;

    public TextTypingAnimation(@NotNull String text, @NotNull String cursor, int idleTicks, int typingSpeedTicks) {
        this.text = text;
        this.cursor = cursor;
        this.idleTimeTicks = idleTicks;
        this.typingSpeed = typingSpeedTicks;

        this.frameIterator = new FrameIterator(createAnimationFrames());
    }

    @Override
    public FrameIterator getFrameIterator() {
        return frameIterator;
    }

    @Override
    public String next() {
        return frameIterator.next();
    }

    @Override
    public void remove() {
        frameIterator.remove();
    }

    @Override
    public void forEachRemaining(Consumer<? super String> action) {
        frameIterator.forEachRemaining(action);
    }

    @Override
    protected void start(List<TextFrame> frames) {
        flickWithCursor(idleTimeTicks, frames, "");
    }

    private void flickWithCursor(int times, List<TextFrame> frames, String text) {
        for (int i = 0; i <= times; i++) {
            frames.add(
                    i % 2 == 0
                            ? TextFrame.of(text + cursor, 10)
                            : TextFrame.of(text, 10)
            );
        }
    }

    public List<TextFrame> createAnimationFrames() {
        // flick with cursor

        List<TextFrame> frames = new ArrayList<>();

        start(frames);

        for (int i = 0; i < text.length(); i++) {
            String frame = text.substring(0, i + 1);
            frames.add(TextFrame.of(frame + cursor, typingSpeed));
        }

        flickWithCursor(idleTimeTicks, frames, text);

        // erase text simulating backspace

        for (int i = text.length() - 1; i >= 0; i--) {
            String frame = text.substring(0, i);
            frames.add(TextFrame.of(frame + cursor, typingSpeed));
        }

        return frames;
    }

}
