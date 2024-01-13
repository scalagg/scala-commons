package gg.scala.commons.scoreboard;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public record TextFrame(String text, long delay) {

    public TextComponent intoTextComponent() {
        return LegacyComponentSerializer
            .legacySection()
            .deserialize(this.text);
    }

    public static TextFrame of(String text, long delay) {
        return new TextFrame(text, delay);
    }
}
