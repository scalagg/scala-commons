package com.newproject.helper.spigot.sidebar.text.impl;

import com.newproject.helper.spigot.sidebar.text.FrameIterator;
import com.newproject.helper.spigot.sidebar.text.TextFrame;
import com.newproject.helper.spigot.sidebar.text.TextIterator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

/**
 * Text sliding animation. WIP
 *
 * @author CatCoder
 */
public class TextSlideAnimation extends TextIterator {

    private final String text;

    private final FrameIterator frameIterator;

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

    public TextSlideAnimation(@NotNull String text) {
        this.text = text;
        this.frameIterator = new FrameIterator(createAnimationFrames());
    }

    private List<TextFrame> createAnimationFrames() {
        // TODO: 17.11.2022 contributions are welcome :)
        throw new UnsupportedOperationException("Unsupported yet");
    }

}
