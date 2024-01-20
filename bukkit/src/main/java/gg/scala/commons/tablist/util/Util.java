package gg.scala.commons.tablist.util;

import gg.scala.commons.tablist.util.data.Profile;
import gg.scala.commons.tablist.util.data.SkinData;
import net.evilblock.cubed.protocol.ViaVersionVersionProvider;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * @author Reximian9k (https://github.com/reximian9k)
 */
public class Util {

    public static int getClientVersion(Player player) {
        return ViaVersionVersionProvider.INSTANCE.getProtocolVersion(player);
    }

    public static Profile getPlayerProfile(Player player) {
        net.minecraft.server.v1_8_R3.EntityPlayer nmsPlayer = ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle();
        com.mojang.authlib.GameProfile nmsProfile = nmsPlayer.getProfile();

        Collection<com.mojang.authlib.properties.Property> texturesProperty = nmsProfile.getProperties().get("textures");
        if (texturesProperty != null && !texturesProperty.isEmpty()) {
            com.mojang.authlib.properties.Property skinProperty = texturesProperty.iterator().next();
            return new Profile(nmsProfile.getId(), nmsProfile.getName(), new SkinData(skinProperty.getValue(),
                    skinProperty.hasSignature() ? skinProperty.getSignature() : null));
        }

        return new Profile(nmsProfile.getId(), nmsProfile.getName());
    }

    public static void sendPacket(Player player, Object packet) {
        ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(
                (net.minecraft.server.v1_8_R3.Packet) packet
        );
    }

}
