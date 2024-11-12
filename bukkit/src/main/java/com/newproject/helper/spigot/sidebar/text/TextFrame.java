package com.newproject.helper.spigot.sidebar.text;

public final class TextFrame {

    private final String text;
    private final long delay;

    private TextFrame(String text, long delay) {
        this.text = text;
        this.delay = delay;
    }

    public static TextFrame of(String text, long delay) {
        return new TextFrame(text, delay);
    }

    public String getText() {
        return this.text;
    }

    public long getDelay() {
        return this.delay;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof TextFrame)) return false;
        final TextFrame other = (TextFrame) o;
        final Object this$text = this.getText();
        final Object other$text = other.getText();
        if (this$text == null ? other$text != null : !this$text.equals(other$text)) return false;
        if (this.getDelay() != other.getDelay()) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $text = this.getText();
        result = result * PRIME + ($text == null ? 43 : $text.hashCode());
        final long $delay = this.getDelay();
        result = result * PRIME + (int) ($delay >>> 32 ^ $delay);
        return result;
    }

    public String toString() {
        return "TextFrame(text=" + this.getText() + ", delay=" + this.getDelay() + ")";
    }
}
