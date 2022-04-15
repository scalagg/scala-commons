package net.evilblock.cubed.util;

import org.bukkit.Bukkit;

/**
 * @author GrowlyX
 * @since 4/15/2022
 */
public enum ServerVersion {
    v_1_7_10(5),
    v_1_8(47), v_1_8_3(47), v_1_8_8(47),
    v_1_9(107), v_1_9_2(109), v_1_9_4(110),
    //1.10 and 1.10.1 are redundant
    v_1_10(210), v_1_10_1(210), v_1_10_2(210),
    v_1_11(315), v_1_11_2(316),
    v_1_12(335), v_1_12_1(338), v_1_12_2(340),
    v_1_13(393), v_1_13_1(401), v_1_13_2(404),
    v_1_14(477), v_1_14_1(480), v_1_14_2(485), v_1_14_3(490), v_1_14_4(498),
    v_1_15(573), v_1_15_1(575), v_1_15_2(578),
    v_1_16(735), v_1_16_1(736), v_1_16_2(751), v_1_16_3(753), v_1_16_4(754), v_1_16_5(754),
    v_1_17(755), v_1_17_1(756),
    v_1_18(757), v_1_18_1(757), v_1_18_2(758),
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

        for (final ServerVersion val : reversedValues) {
            String valName = val.name().substring(2).replace("_", ".");
            if (Bukkit.getBukkitVersion().contains(valName)) {
                return val;
            }
        }

        return ERROR;
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
