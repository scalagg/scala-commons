package gg.scala.commons.tablist.util.data;

import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Class representing essential Minecraft account profile details
 *
 * @author Reximian9k (https://github.com/reximian9k)
 */
public class Profile {

    public static Profile createOffline(String name) {
        return new Profile(UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8)), name);
    }

    public static Profile createOffline(String name, SkinData skinData) {
        return new Profile(UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8)), name, skinData);
    }

    private final UUID id;
    private final String name;
    private final SkinData skinData;

    public Profile(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.skinData = null;
    }

    public Profile(UUID id, String name, @Nullable SkinData skinData) {
        this.id = id;
        this.name = name;
        this.skinData = skinData;
    }

    /**
     * @return {@code true} if account id is present
     */
    public boolean hasId() {
        return id != null;
    }

    public UUID getId() {
        return id;
    }

    /**
     * @return {@code true} if account name is present
     */
    public boolean hasName() {
        return name != null;
    }

    public String getName() {
        return name;
    }

    /**
     * @return {@code true} if account skin data is present
     */
    public boolean hasSkin() {
        return skinData != null;
    }

    /**
     * @return The account skin data
     */
    @Nullable
    public SkinData getSkinData() {
        return skinData;
    }

}
