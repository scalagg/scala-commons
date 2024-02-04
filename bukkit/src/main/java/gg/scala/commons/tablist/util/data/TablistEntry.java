package gg.scala.commons.tablist.util.data;

import org.bukkit.GameMode;

import java.util.UUID;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public class TablistEntry {

    private UUID userId;
    private String name;
    private SkinData icon;
    private GameMode gameMode;
    private int ping;
    private String displayName;

    private TablistEntry() { }

    public TablistEntry(UUID userId, String name, SkinData icon, GameMode gameMode, int ping, String displayName) {
        this.userId = userId;
        this.name = name;
        this.icon = icon;
        this.gameMode = gameMode;
        this.ping = ping;
        this.displayName = displayName;
    }

    public TablistEntry(TablistEntry other) {
        this.userId = other.userId;
        this.name = other.name;
        this.icon = other.icon;
        this.gameMode = other.gameMode;
        this.ping = other.ping;
        this.displayName = other.displayName;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SkinData getIcon() {
        return icon;
    }

    public void setIcon(SkinData icon) {
        this.icon = icon;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public boolean hasDisplayName() {
        return displayName != null;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final TablistEntry entry = new TablistEntry();

        public Builder userId(UUID userId) {
            entry.userId = userId;
            return this;
        }

        public Builder name(String name) {
            entry.name = name;
            return this;
        }

        public Builder icon(SkinData icon) {
            entry.icon = icon;
            return this;
        }

        public Builder gameMode(GameMode gameMode) {
            entry.gameMode = gameMode;
            return this;
        }

        public Builder ping(int ping) {
            entry.ping = ping;
            return this;
        }

        public Builder displayName(String displayName) {
            entry.displayName = displayName;
            return this;
        }

        public TablistEntry build() {
            return new TablistEntry(entry);
        }

    }

}
