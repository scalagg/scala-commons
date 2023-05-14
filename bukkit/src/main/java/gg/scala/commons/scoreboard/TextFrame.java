package gg.scala.commons.scoreboard;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class TextFrame {

    private final String text;
    private final long delay;

    public TextFrame(String text, long delay) {
        this.text = text;
        this.delay = delay;
    }

    public TextComponent intoTextComponent() {
        return LegacyComponentSerializer
                .legacySection()
                .deserialize(this.text);
    }

    public long getDelay() {
        return delay;
    }

    public String getText() {
        return text;
    }

    public static TextFrame of(String text, long delay) {
        return new TextFrame(text, delay);
    }
}
