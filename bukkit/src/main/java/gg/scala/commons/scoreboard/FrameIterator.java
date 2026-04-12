package gg.scala.commons.scoreboard;

import com.google.common.collect.Iterators;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public final class FrameIterator implements Iterator<String> {

    private final Iterator<TextFrame> frameIterator;

    private long currentFrameDelayTicks = 0;
    private TextFrame currentFrame;

    public FrameIterator(@NotNull Iterable<TextFrame> frames) {
        this.frameIterator = Iterators.cycle(frames);
    }

    public long getCurrentFrameDelayTicks() {
        return currentFrameDelayTicks;
    }

    public TextFrame getCurrentFrame() {
        return currentFrame;
    }

    @Override
    public String next() {
        if (currentFrame == null || --currentFrameDelayTicks <= 0) {
            currentFrame = frameIterator.next();
            currentFrameDelayTicks = currentFrame.delay();
        }

        return currentFrame.text();
    }

    @Override
    public boolean hasNext() {
        return true;
    }
}
