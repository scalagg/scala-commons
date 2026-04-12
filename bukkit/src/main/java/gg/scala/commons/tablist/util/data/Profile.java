package gg.scala.commons.tablist.util.data;

import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

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

    public boolean hasId() {
        return id != null;
    }

    public UUID getId() {
        return id;
    }

    public boolean hasName() {
        return name != null;
    }

    public String getName() {
        return name;
    }

    public boolean hasSkin() {
        return skinData != null;
    }

    @Nullable
    public SkinData getSkinData() {
        return skinData;
    }

}
