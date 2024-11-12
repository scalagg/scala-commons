/*
 * This file is part of helper, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.helper.modern;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.ScoreboardAction;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import me.lucko.helper.protocol.Protocol;
import me.lucko.helper.utils.annotation.NonnullByDefault;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Implements {@link ScoreboardObjective} using ProtocolLib.
 */
@NonnullByDefault
public class PacketScoreboardObjective implements ScoreboardObjective {

    // the "Entity name" in the Update Score packet is limited to 40 chars
    private static final int MAX_SCORE_LENGTH = 40;
    // the "Objective Value" in the ScoreboardObjective packet is limited to 32 chars
    private static final int MAX_NAME_LENGTH = 32;

    private static String trimScore(String name) {
        return name;//name.length() > MAX_SCORE_LENGTH ? name.substring(0, MAX_SCORE_LENGTH) : name;
    }

    private static String trimName(String name) {
        return name.length() > MAX_NAME_LENGTH ? name.substring(0, MAX_NAME_LENGTH) : name;
    }

    // the id of this objective
    private final String id;
    // the current scores being shown
    private final Map<Component, Integer> scores = Collections.synchronizedMap(new HashMap<>());
    // a set of the players subscribed to & receiving updates for this objective
    private final Set<Player> subscribed = Collections.synchronizedSet(new HashSet<>());
    // if players should be automatically subscribed
    private boolean autoSubscribe;
    // the current display name
    private Component displayName;
    // the current display slot
    private DisplaySlot displaySlot;

    /**
     * Creates a new scoreboard objective
     *
     * @param id            the id of this objective
     * @param displayName   the initial display name
     * @param displaySlot   the initial display slot
     * @param autoSubscribe if players should be automatically subscribed
     */
    public PacketScoreboardObjective(String id, Component displayName, DisplaySlot displaySlot, boolean autoSubscribe) {
        Objects.requireNonNull(id, "id");
        Preconditions.checkArgument(id.length() <= 16, "id cannot be longer than 16 characters");

        this.id = id;
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.displaySlot = Objects.requireNonNull(displaySlot, "displaySlot");
        this.autoSubscribe = autoSubscribe;
    }

    /**
     * Creates a new scoreboard objective
     *
     * @param id          the id of this objective
     * @param displayName the initial display name
     * @param displaySlot the initial display slot
     */
    public PacketScoreboardObjective(String id, Component displayName, DisplaySlot displaySlot) {
        this(id, displayName, displaySlot, true);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean shouldAutoSubscribe() {
        return this.autoSubscribe;
    }

    @Override
    public void setAutoSubscribe(boolean autoSubscribe) {
        this.autoSubscribe = autoSubscribe;
    }

    @Override
    public Component getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(Component displayName) {
        Objects.requireNonNull(displayName, "displayName");
        if (this.displayName.equals(displayName)) {
            return;
        }

        this.displayName = displayName;
        Protocol.broadcastPacket(this.subscribed, newObjectivePacket(UpdateType.UPDATE));
    }

    @Override
    public DisplaySlot getDisplaySlot() {
        return this.displaySlot;
    }

    @Override
    public void setDisplaySlot(DisplaySlot displaySlot) {
        Objects.requireNonNull(displaySlot, "displaySlot");
        if (this.displaySlot == displaySlot) {
            return;
        }

        this.displaySlot = displaySlot;
        Protocol.broadcastPacket(this.subscribed, newDisplaySlotPacket(displaySlot));
    }

    @Override
    public Map<Component, Integer> getScores() {
        return ImmutableMap.copyOf(this.scores);
    }

    @Override
    public boolean hasScore(String name) {
        Objects.requireNonNull(name, "name");
        return this.scores.containsKey(LegacyComponentSerializer.legacyAmpersand().deserialize(name));
    }

    @Nullable
    @Override
    public Integer getScore(String name) {
        Objects.requireNonNull(name, "name");
        return this.scores.get(LegacyComponentSerializer.legacyAmpersand().deserialize(name));
    }

    @Override
    public void setScore(Component name, int value) {
        Objects.requireNonNull(name, "name");

        Integer oldValue = this.scores.put(name, value);
        if (oldValue != null && oldValue == value) {
            return;
        }

        Protocol.broadcastPacket(this.subscribed, newScorePacket(name, value, ScoreboardAction.CHANGE));
    }

    @Override
    public boolean removeScore(Component name) {
        Objects.requireNonNull(name, "name");

        final Integer slot = this.scores.get(name);
        if (slot == null) {
            return false;
        }

        Protocol.broadcastPacket(this.subscribed, newScorePacket(name, slot, ScoreboardAction.REMOVE));
        return true;
    }

    @Override
    public void clearScores() {
        this.scores.clear();

        Protocol.broadcastPacket(this.subscribed, newObjectivePacket(UpdateType.REMOVE));
        for (Player player : this.subscribed) {
            subscribe(player);
        }
    }

    @Override
    public void applyScores(Map<Component, Integer> scores) {
        Objects.requireNonNull(scores, "scores");

//        Set<Component> toRemove = new HashSet<>(getScores().keySet());
//        for (Map.Entry<Component, Integer> score : scores.entrySet()) {
//            toRemove.remove(score.getKey());
//        }
//        for (Component name : toRemove) {
//            removeScore(name);
//        }
        // todo: This is really janky, there has to be a better way
        clearScores();
        for (Map.Entry<Component, Integer> score : scores.entrySet()) {
            setScore(score.getKey(), score.getValue());
        }
    }

    @Override
    public void applyLines(Component... lines) {
        applyLines(Arrays.asList(lines));
    }

    @Override
    public void applyLines(Collection<Component> lines) {
        Objects.requireNonNull(lines, "lines");
        Map<Component, Integer> scores = new HashMap<>();
        int i = lines.size();
        for (Component line : lines) {
            scores.put(line, i--);
        }
        applyScores(scores);
    }

    @Override
    public void subscribe(Player player) {
        Objects.requireNonNull(player, "player");
        Protocol.sendPacket(player, newObjectivePacket(UpdateType.CREATE));
        Protocol.sendPacket(player, newDisplaySlotPacket(getDisplaySlot()));
        for (Map.Entry<Component, Integer> score : getScores().entrySet()) {
            Protocol.sendPacket(player, newScorePacket(score.getKey(), score.getValue(), ScoreboardAction.CHANGE));
        }
        this.subscribed.add(player);
    }

    @Override
    public void unsubscribe(Player player) {
        unsubscribe(player, false);
    }

    @Override
    public void unsubscribe(Player player, boolean fast) {
        Objects.requireNonNull(player, "player");
        if (!this.subscribed.remove(player) || fast) {
            return;
        }

        Protocol.sendPacket(player, newObjectivePacket(UpdateType.REMOVE));
    }

    @Override
    public void unsubscribeAll() {
        Protocol.broadcastPacket(this.subscribed, newObjectivePacket(UpdateType.REMOVE));
        this.subscribed.clear();
    }

    private PacketContainer newObjectivePacket(UpdateType mode) {
        // http://wiki.vg/Protocol#Scoreboard_Objective
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);

        // set name - limited to String(16)
        packet.getStrings().write(0, getId());

        // set mode - 0 to create the scoreboard. 1 to remove the scoreboard. 2 to update the display text.
        packet.getBytes().write(0, mode.getCode());

        // set display name - Component
        packet.getChatComponents().write(0, PacketScoreboard.toComponent(getDisplayName()));

        // set type - either "integer" or "hearts"
        packet.getEnumModifier(HealthDisplay.class, 2).write(0, HealthDisplay.INTEGER);

        return packet;
    }

    private PacketContainer newScorePacket(Component name, int value, ScoreboardAction action) {
        // http://wiki.vg/Protocol#Update_Score
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);

        // set "Entity name" - aka the name of the score - limited to 40.
        packet.getStrings().write(0, String.valueOf(value));

        // set objective name - The name of the objective the score belongs to
        packet.getStrings().write(1, getId());

        // set value of the score - The score to be displayed next to the entry. Only sent when Action does not equal 1.
        packet.getIntegers().write(0, value);

        // has display name
        packet.getBooleans().write(0, true);

        // display name
        packet.getChatComponents().write(0, PacketScoreboard.toComponent(name));

        // has number format
        packet.getBooleans().write(1, false);

        // set the action - 0 to create/update an item. 1 to remove an item.
        packet.getScoreboardActions().write(0, action);

        return packet;
    }

    private PacketContainer newDisplaySlotPacket(DisplaySlot displaySlot) {
        // http://wiki.vg/Protocol#Display_Scoreboard
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);

        // set display slot - The position of the objective. One of the following: list, sidebar, belowName.
        packet.getEnumModifier(DisplaySlot.class, 0).write(0, displaySlot);

        // set objective name - The unique name for the scoreboard to be displayed.
        packet.getStrings().write(0, getId());

        return packet;
    }

    private enum UpdateType {
        CREATE((byte) 0),
        REMOVE((byte) 1),
        UPDATE((byte) 2);

        private final byte code;

        UpdateType(byte code) {
            this.code = code;
        }

        public byte getCode() {
            return this.code;
        }
    }

    private enum HealthDisplay {
        INTEGER, HEARTS
    }

}
