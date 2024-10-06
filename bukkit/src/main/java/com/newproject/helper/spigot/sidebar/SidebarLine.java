package com.newproject.helper.spigot.sidebar;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.newproject.helper.spigot.sidebar.protocol.ChannelInjector;
import com.newproject.helper.spigot.sidebar.protocol.ScoreNumberFormat;
import com.newproject.helper.spigot.sidebar.protocol.ScoreboardPackets;
import com.newproject.helper.spigot.sidebar.text.TextProvider;
import com.newproject.helper.spigot.sidebar.util.lang.ThrowingFunction;
import com.newproject.helper.spigot.sidebar.util.lang.ThrowingPredicate;
import com.newproject.helper.spigot.sidebar.util.lang.ThrowingSupplier;
import io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class SidebarLine<R> {

    private final String teamName;

    private int score = -1;

    private final int index;
    private final boolean staticText;

    // for internal use
    BukkitTask updateTask;

    private ThrowingFunction<Player, R, Throwable> updater;
    private ThrowingPredicate<Player, Throwable> displayCondition;
    private final TextProvider<R> textProvider;
    private ScoreNumberFormat scoreNumberFormat;
    private Function<Player, R> scoreNumberFormatter;

    SidebarLine(@NotNull ThrowingFunction<Player, R, Throwable> updater,
                @NotNull String teamName,
                boolean staticText,
                int index,
                @NotNull TextProvider<R> textProvider,
                @NotNull ThrowingPredicate<Player, Throwable> displayCondition) {
        this.updater = updater;
        this.teamName = teamName;
        this.staticText = staticText;
        this.index = index;
        this.displayCondition = displayCondition;
        this.textProvider = textProvider;
    }

    public SidebarLine<R> scoreNumberFormatBlank() {
        this.scoreNumberFormat = null;
        this.scoreNumberFormatter = null;
        return this;
    }

    public SidebarLine<R> scoreNumberFormatFixed(@NotNull Function<Player, R> scoreNumberFormatter) {
        this.scoreNumberFormat = ScoreNumberFormat.FIXED;
        this.scoreNumberFormatter = scoreNumberFormatter;
        return this;
    }

    public SidebarLine<R> scoreNumberFormatStyled(@NotNull Function<Player, R> scoreNumberFormatter) {
        this.scoreNumberFormat = ScoreNumberFormat.STYLED;
        this.scoreNumberFormatter = scoreNumberFormatter;
        return this;
    }

    public BukkitTask updatePeriodically(long delay, long period, @NotNull Sidebar<R> sidebar) {
        Preconditions.checkState(!isStaticText(), "Cannot set updater for static text line");

        if (updateTask != null) {
            Preconditions.checkState(updateTask.isCancelled(),
                    "Update task for line %s is already running. Cancel it first.", this);
            sidebar.taskIds.remove(updateTask.getTaskId());
        }

        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(sidebar.getPlugin(),
                () -> sidebar.updateLine(this), delay, period);

        this.updateTask = task;

        sidebar.bindBukkitTask(task);

        return task;
    }

    /**
     * Sets visibility predicate for this line. Visibility predicate is a function that takes player
     * as an argument and returns boolean value. If predicate returns true, line will be visible for
     * this player, otherwise - invisible.
     *
     * @param displayCondition - visibility predicate
     */
    public void setDisplayCondition(@NotNull ThrowingPredicate<Player, Throwable> displayCondition) {
        this.displayCondition = displayCondition;
    }

    /**
     * Sets updater for this line. Updater is a function that takes player as an argument and returns
     * text that will be displayed for this player.
     *
     * @param updater - updater function
     */
    public void setUpdater(@NotNull ThrowingFunction<Player, R, Throwable> updater) {
        Preconditions.checkState(!isStaticText(), "Cannot set updater for static text line");
        this.updater = updater;
    }

    /**
     * Sets updater for this line without player parameter
     *
     * @param updater - updater function
     */
    public void setUpdater(@NotNull ThrowingSupplier<R, Throwable> updater) {
        Preconditions.checkState(!isStaticText(), "Cannot set updater for static text line");
        this.updater = player -> updater.get();
    }

    void updateTeam(@NotNull Player player, @NotNull String objective) throws Throwable {
        boolean visible = displayCondition.test(player);

        if (!isStaticText() && visible) {
            R text = updater.apply(player);
            sendPacket(player, ScoreboardPackets.createTeamPacket(
                    ScoreboardPackets.TEAM_UPDATED, index, teamName,
                    player, text, textProvider));
        }

        if (!visible) {
            // if player doesn't meet display condition, remove score
            sendPacket(player, ScoreboardPackets.createScorePacket(
                    player, 1, objective, score, index, textProvider, scoreNumberFormat, null));
            return;
        }

        sendPacket(player, ScoreboardPackets.createScorePacket(
                player, 0, objective, score, index, textProvider, scoreNumberFormat, scoreNumberFormatter));
    }

    void removeTeam(@NotNull Player player, @NotNull String objective) {
        sendPacket(player, ScoreboardPackets.createScorePacket(
                player, 1, objective, score, index, textProvider, null, null));

        sendPacket(player, ScoreboardPackets.createTeamPacket(ScoreboardPackets.TEAM_REMOVED, index, teamName,
                player, null, textProvider));
    }

    void createTeam(@NotNull Player player, @NotNull String objective) throws Throwable {
        boolean visible = displayCondition.test(player);

        R text = visible ? updater.apply(player) : textProvider.emptyMessage();

        sendPacket(player, ScoreboardPackets.createTeamPacket(ScoreboardPackets.TEAM_CREATED, index, teamName,
                player, text, textProvider));

        if (visible) {
            sendPacket(player, ScoreboardPackets.createScorePacket(
                    player, 0, objective, score, index, textProvider, scoreNumberFormat, scoreNumberFormatter));
        }
    }

    static void sendPacket(@NotNull Player player, @NotNull ByteBuf packet) {
        try {
            if (packet == null) {
                throw new IllegalStateException("No null packet bro" + packet.toString());
            }

            ChannelInjector.IMP.sendPacket(player, packet);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public String getTeamName() {
        return this.teamName;
    }

    public int getScore() {
        return this.score;
    }

    public int getIndex() {
        return this.index;
    }

    public boolean isStaticText() {
        return this.staticText;
    }

    public BukkitTask getUpdateTask() {
        return this.updateTask;
    }

    public ThrowingFunction<Player, R, Throwable> getUpdater() {
        return this.updater;
    }

    public ThrowingPredicate<Player, Throwable> getDisplayCondition() {
        return this.displayCondition;
    }

    public TextProvider<R> getTextProvider() {
        return this.textProvider;
    }

    public ScoreNumberFormat getScoreNumberFormat() {
        return this.scoreNumberFormat;
    }

    public Function<Player, R> getScoreNumberFormatter() {
        return this.scoreNumberFormatter;
    }

    public String toString() {
        return "SidebarLine(teamName=" + this.getTeamName() + ", score=" + this.getScore() + ", index=" + this.getIndex() + ", staticText=" + this.isStaticText() + ", updateTask=" + this.getUpdateTask() + ", updater=" + this.getUpdater() + ", displayCondition=" + this.getDisplayCondition() + ", textProvider=" + this.getTextProvider() + ", scoreNumberFormat=" + this.getScoreNumberFormat() + ", scoreNumberFormatter=" + this.getScoreNumberFormatter() + ")";
    }

    void setScore(int score) {
        this.score = score;
    }
}
