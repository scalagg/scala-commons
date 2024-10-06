package net.evilblock.cubed.util;

import net.evilblock.cubed.ScalaCommonsSpigot;
import org.bukkit.Bukkit;

/**
 * @author GrowlyX
 * @since 4/15/2022
 */
public enum ServerVersion {
    v1_7_10(5),
    v1_8(47), v1_8_R3(47), v1_8_8(47),
    v1_9(107), v1_9_R2(109), v1_9_R4(110),
    //1.10 and 1.10.1 are redundant
    v1_10(210), v1_10_R1(210), v1_10_R2(210),
    v1_11(315), v1_11_R2(316),
    v1_12(335), v1_12_R1(338), v1_12_R2(340),
    v1_13(393), v1_13_R1(401), v1_13_R2(404),
    v1_14(477), v1_14_R1(480), v1_14_R2(485), v1_14_R3(490), v1_14_R4(498),
    v1_15(573), v1_15_R1(575), v1_15_R2(578),
    v1_16(735), v1_16_R1(736), v1_16_R2(751), v1_16_R3(753), v1_16_R4(754), v1_16_R5(754),
    v1_17(755), v1_17_R1(756),
    v1_18(757), v1_18_R1(757), v1_18_R2(758),
    v1_19(759), v1_19_R1(760),
    v1_20(763), v1_20_R3(765), v1_20_R4(765), v1_20_R5(766), v1_20_R6(766),
    v1_21(767),
    ERROR(-1);

    private static final String NMS_VERSION_SUFFIX = Bukkit.getServer().getClass().getPackage().getName()
            .replace(".", ",").split(",")[3];

    private static final ServerVersion[] VALUES = values();

    public static ServerVersion[] reversedValues = new ServerVersion[VALUES.length];
    private static ServerVersion cachedVersion;

    private final int protocolVersion;

    ServerVersion(int protocolId) {
        this.protocolVersion = protocolId;
    }

    private static ServerVersion getVersionNoCache() {
        if (reversedValues[0] == null) {
            reversedValues = ServerVersion.reverse();
        }

        ScalaCommonsSpigot.getInstance().getLogger().info(
            "[Versioning] Attempting to parse version based on: " + Bukkit.getBukkitVersion()
        );

        for (final ServerVersion val : reversedValues) {
            // v1_19_R2 -> 1_19_R2 -> 1_19.2 -> 1.19.2
            final String valName = val.name()
                    .replace("v", "")
                    .replace("_R", ".")
                    .replace("_", ".");

            if (Bukkit.getBukkitVersion().contains(valName)) {
                ScalaCommonsSpigot.getInstance().getLogger().info(
                    "[Versioning] Parsed version " + val.name() + "!"
                );
                return val;
            }
        }

        ScalaCommonsSpigot.getInstance().getLogger().info(
            "[Versioning] Failed to parse version. We're defaulting to 1.8.8 R3."
        );

        return v1_8_R3;
    }

    /**
     * Get the server version.
     * If PacketEvents has already attempted resolving, return the cached version.
     * If PacketEvents hasn't already attempted resolving, it will resolve it, cache it and return the version.
     *
     * @return Server Version. (always cached)
     */
    public static ServerVersion getVersion() {
        if (cachedVersion == null) {
            cachedVersion = getVersionNoCache();
        }

        return cachedVersion;
    }

    /**
     * The values in this enum in reverse.
     *
     * @return Reversed server version enum values.
     */
    private static ServerVersion[] reverse() {
        ServerVersion[] array = values();
        int i = 0;
        int j = array.length - 1;
        ServerVersion tmp;
        while (j > i) {
            tmp = array[j];
            array[j--] = array[i];
            array[i++] = tmp;
        }
        return array;
    }

    public static String getNMSSuffix() {
        return NMS_VERSION_SUFFIX;
    }

    public static String getNMSDirectory() {
        return "net.minecraft.server." + getNMSSuffix();
    }

    public static String getOBCDirectory() {
        return "org.bukkit.craftbukkit." + (getNMSSuffix());
    }

    public static ServerVersion getLatest() {
        return reversedValues[0];
    }

    public static ServerVersion getOldest() {
        return values()[0];
    }

    /**
     * Get this server version's protocol version.
     *
     * @return Protocol version.
     */
    public int getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * Is this server version newer than the compared server version?
     * This method simply checks if this server version's protocol version is greater than
     * the compared server version's protocol version.
     *
     * @param target Compared server version.
     * @return Is this server version newer than the compared server version.
     */
    public boolean isNewerThan(ServerVersion target) {
        /*
         * Some server versions have the same protocol version in the minecraft protocol.
         * We still need this method to work in such cases.
         * We first check if this is the case, if the protocol versions aren't the same, we can just use the protocol versions
         * to compare the server versions.
         */
        if (target.protocolVersion != protocolVersion || this == target) {
            return protocolVersion > target.protocolVersion;
        }

        /*
         * The server versions unfortunately have the same protocol version.
         * We need to look at this "reversedValues" variable.
         * The reversed values variable is an array containing all enum constants in this enum but in a reversed order.
         * I already made this variable a while ago for a different usage, you can check that out.
         * The first one we find in the array is the newer version.
         */
        for (ServerVersion version : reversedValues) {
            if (version == target) {
                return false;
            }
            if (version == this) return true;
        }

        return false;
    }

    /**
     * Is this server version older than the compared server version?
     * This method simply checks if this server version's protocol version is less than
     * the compared server version's protocol version.
     *
     * @param target Compared server version.
     * @return Is this server version older than the compared server version.
     */
    public boolean isOlderThan(ServerVersion target) {
        /*
         * Some server versions have the same protocol version in the minecraft protocol.
         * We still need this method to work in such cases.
         * We first check if this is the case, if the protocol versions aren't the same, we can just use the protocol versions
         * to compare the server versions.
         */
        if (target.protocolVersion != protocolVersion || this == target) {
            return protocolVersion < target.protocolVersion;
        }
        /*
         * The server versions unfortunately have the same protocol version.
         * We look at all enum constants in the ServerVersion enum in the order they have been defined in.
         * The first one we find in the array is the newer version.
         */
        for (ServerVersion version : VALUES) {
            if (version == this) {
                return true;
            } else if (version == target) {
                return false;
            }
        }
        return false;
    }

    /**
     * Is this server version newer than or equal to the compared server version?
     * This method simply checks if this server version's protocol version is greater than or equal to
     * the compared server version's protocol version.
     *
     * @param target Compared server version.
     * @return Is this server version newer than or equal to the compared server version.
     */
    public boolean isNewerThanOrEquals(ServerVersion target) {
        return this == target || isNewerThan(target);
    }

    /**
     * Is this server version older than or equal to the compared server version?
     * This method simply checks if this server version's protocol version is older than or equal to
     * the compared server version's protocol version.
     *
     * @param target Compared server version.
     * @return Is this server version older than or equal to the compared server version.
     */
    public boolean isOlderThanOrEquals(ServerVersion target) {
        return this == target || isOlderThan(target);
    }

    /**
     * Deprecated, please use {@link #isNewerThan(ServerVersion)}
     *
     * @param target Compared version.
     * @return Is this server version newer than the compared server version.
     * @deprecated Rename...
     **/
    @Deprecated
    public boolean isHigherThan(final ServerVersion target) {
        return isNewerThan(target);
    }

    /**
     * Deprecated, Please use {@link #isNewerThanOrEquals(ServerVersion)}
     *
     * @param target Compared server version.
     * @return Is this server version newer than or equal to the compared server version.
     * @deprecated Rename...
     */
    @Deprecated
    public boolean isHigherThanOrEquals(final ServerVersion target) {
        return isNewerThanOrEquals(target);
    }

    /**
     * Deprecated... Please use {@link #isOlderThan(ServerVersion)}
     *
     * @param target Compared server version.
     * @return Is this server version older than the compared server version.
     * @deprecated Rename....
     */
    @Deprecated
    public boolean isLowerThan(final ServerVersion target) {
        return isOlderThan(target);
    }

    /**
     * Deprecated, please use {@link #isOlderThanOrEquals(ServerVersion)}
     *
     * @param target Compared server version.
     * @return Is this server version older than or equal to the compared server version.
     * @deprecated Rename...
     */
    @Deprecated
    public boolean isLowerThanOrEquals(final ServerVersion target) {
        return isOlderThanOrEquals(target);
    }
}
