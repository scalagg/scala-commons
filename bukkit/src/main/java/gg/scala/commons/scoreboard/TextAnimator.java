package gg.scala.commons.scoreboard;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.evilblock.cubed.util.bukkit.Tasks.asyncTimer;

/**
 * Animates a TextIterator using Bukkit's async scheduler.
 *
 * @author GrowlyX
 * @since 5/14/2023
 */
public class TextAnimator {
    private final TextIterator iterator;
    private @Nullable BukkitTask bukkitTask;

    private TextAnimator(@NotNull TextIterator iterator) {
        this.iterator = iterator;
    }

    public TextFrame current() {
        return iterator
                .getFrameIterator()
                .getCurrentFrame();
    }

    public TextComponent currentIntoTextComponent() {
        return current().intoTextComponent();
    }

    public TextAnimator dispose() {
        Preconditions.checkNotNull(
                bukkitTask,
                "TextAnimator is not yet scheduled"
        );

        bukkitTask.cancel();
        bukkitTask = null;
        return this;
    }

    public TextAnimator schedule() {
        Preconditions.checkState(
                bukkitTask != null,
                "TextAnimator was already scheduled"
        );

        asyncTimer(iterator::next, 0L, 1L);
        return this;
    }

    public static TextAnimator of(TextIterator iterator) {
        return new TextAnimator(iterator);
    }
}
