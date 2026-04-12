package gg.scala.commons.tablist.util;

import gg.scala.commons.tablist.util.data.Profile;
import gg.scala.commons.tablist.util.data.SkinData;
import net.evilblock.cubed.protocol.ViaVersionVersionProvider;
import net.evilblock.cubed.util.Reflection;
import net.evilblock.cubed.util.nms.MinecraftReflection;
import org.bukkit.entity.Player;
import java.util.Collection;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Util {

    private static final Class<?> CRAFT_PLAYER_CLASS = MinecraftReflection.getCraftBukkitClass("entity.CraftPlayer");
    private static final Class<?> ENTITY_PLAYER_CLASS = !MinecraftReflection.isModern()
            ? MinecraftReflection.getNMSClass("EntityPlayer")
            : Reflection.getClassSuppressed("net.minecraft.server.level.ServerPlayer");
    private static final Method GET_HANDLE_METHOD = Reflection.getMethodSuppressed(CRAFT_PLAYER_CLASS, "getHandle");
    private static final Field PLAYER_CONNECTION_FIELD = Reflection.getDeclaredField(
            ENTITY_PLAYER_CLASS,
            MinecraftReflection.isModern() ? "c" : (Reflection.getDeclaredField(ENTITY_PLAYER_CLASS, "playerConnection") != null ? "playerConnection" : "b")
    ) != null ? Reflection.getDeclaredField(
            ENTITY_PLAYER_CLASS,
            MinecraftReflection.isModern() ? "c" : (Reflection.getDeclaredField(ENTITY_PLAYER_CLASS, "playerConnection") != null ? "playerConnection" : "b")
    ) : Reflection.getDeclaredField(ENTITY_PLAYER_CLASS, "b");

    private static final Method SEND_PACKET_METHOD = PLAYER_CONNECTION_FIELD != null
            ? Reflection.getMethodSuppressed(PLAYER_CONNECTION_FIELD.getType(), "sendPacket", Reflection.getClassSuppressed("net.minecraft.network.protocol.Packet") != null
                ? Reflection.getClassSuppressed("net.minecraft.network.protocol.Packet")
                : MinecraftReflection.getNMSClass("Packet"))
            : null;

    public static int getClientVersion(Player player) {
        return ViaVersionVersionProvider.INSTANCE.getProtocolVersion(player);
    }

    public static Profile getPlayerProfile(Player player) {
        if (MinecraftReflection.isModern()) {
            try {
                Object profile = Reflection.callMethod(player, "getPlayerProfile");
                if (profile != null) {
                    Object textures = Reflection.callMethod(profile, "getTextures");
                    Object skin = textures != null ? Reflection.callMethod(textures, "getSkin") : null;
                    java.util.UUID id = (java.util.UUID) (Reflection.callMethod(profile, "getId") != null
                            ? Reflection.callMethod(profile, "getId")
                            : (Reflection.callMethod(profile, "getUniqueId") != null
                                ? Reflection.callMethod(profile, "getUniqueId")
                                : null));
                    String name = (String) (Reflection.callMethod(profile, "getName") != null
                            ? Reflection.callMethod(profile, "getName")
                            : null);

                    if (skin != null) {
                        String value = encodeSkinUrl(skin.toString());
                        return new Profile(id, name, new SkinData(value, null));
                    }

                    return new Profile(id, name);
                }
            } catch (Throwable ignored) {
            }
        }

        try {
            Object nmsPlayer = GET_HANDLE_METHOD.invoke(player);
            Object nmsProfile = Reflection.callMethod(nmsPlayer, "getProfile");
            if (nmsProfile == null) {
                nmsProfile = Reflection.getDeclaredFieldValue(nmsPlayer, "gameProfile");
            }

            java.util.UUID id = (java.util.UUID) (Reflection.callMethod(nmsProfile, "getId") != null
                    ? Reflection.callMethod(nmsProfile, "getId")
                    : (Reflection.callMethod(nmsProfile, "id") != null
                        ? Reflection.callMethod(nmsProfile, "id")
                        : Reflection.getDeclaredFieldValue(nmsProfile, "id")));
            String name = (String) (Reflection.callMethod(nmsProfile, "getName") != null
                    ? Reflection.callMethod(nmsProfile, "getName")
                    : (Reflection.callMethod(nmsProfile, "name") != null
                        ? Reflection.callMethod(nmsProfile, "name")
                        : Reflection.getDeclaredFieldValue(nmsProfile, "name")));

            Object properties = Reflection.callMethod(nmsProfile, "getProperties");
            if (properties == null) {
                properties = Reflection.callMethod(nmsProfile, "properties");
            }
            if (properties == null) {
                properties = Reflection.getDeclaredFieldValue(nmsProfile, "properties");
            }

            Collection<?> texturesProperty = (Collection<?>) Reflection.callMethod(properties, "get", "textures");
            if (texturesProperty != null && !texturesProperty.isEmpty()) {
                Object skinProperty = texturesProperty.iterator().next();
                String value = (String) Reflection.callMethod(skinProperty, "getValue");
                Boolean hasSignature = (Boolean) Reflection.callMethod(skinProperty, "hasSignature");
                String signature = hasSignature != null && hasSignature ? (String) Reflection.callMethod(skinProperty, "getSignature") : null;
                return new Profile(id, name, new SkinData(value, signature));
            }

            return new Profile(id, name);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to read player profile", exception);
        }
    }

    private static String encodeSkinUrl(String url) {
        String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}";
        return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = GET_HANDLE_METHOD.invoke(player);
            Object connection = PLAYER_CONNECTION_FIELD.get(handle);
            SEND_PACKET_METHOD.invoke(connection, packet);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to send packet", exception);
        }
    }

}
