package net.evilblock.cubed.util.nms.block;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import net.evilblock.cubed.util.Reflection;
import net.evilblock.cubed.util.nms.MinecraftReflection;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.PacketPlayOutMultiBlockChange;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.entity.Player;
import java.lang.reflect.Field;

public final class BlockChangeBuilder {

    private static Field infoArrayField = Reflection.getDeclaredField(MinecraftReflection.getNMSClass("PacketPlayOutMultiBlockChange"), "b");

    private final Multimap<Long, Entry> changes;
    private final World world;

    public BlockChangeBuilder(World world) {
        this.changes = LinkedListMultimap.create();
        Preconditions.checkArgument(world != null, "World cannot be null");
        this.world = world;
    }

    public BlockChangeBuilder addChange(int x, int y, int z, int newType, int newData) {
        long chunkKey = LongHash.toLong(x >> 4, z >> 4);
        int xOffset = x & 15;
        int zOffset = z & 15;
        short locationKey = (short) (xOffset << 12 | zOffset << 8 | y);
        this.changes.get(chunkKey).add(new Entry(locationKey, newType + (newData << 12)));
        return this;
    }

    public void sendUpdate(Player player, boolean sendOriginalIds) {
        for (long chunkKey : changes.keySet()) {
            int chunkX = LongHash.msw(chunkKey);
            int chunkZ = LongHash.lsw(chunkKey);

            PacketPlayOutMultiBlockChange packet;
            Chunk chunk = ((CraftWorld) world).getHandle().getChunkAt(chunkX, chunkZ);
            int counter = 0;

            if (!sendOriginalIds) {
                PacketPlayOutMultiBlockChange.MultiBlockChangeInfo[] info = new PacketPlayOutMultiBlockChange.MultiBlockChangeInfo[changes.get(chunkKey).size()];
                packet = new PacketPlayOutMultiBlockChange(0, null, chunk);

                for (Entry entry : changes.get(chunkKey)) {
                    IBlockData data = Block.getByCombinedId(entry.blockType);
                    info[counter++] = packet.new MultiBlockChangeInfo(entry.locationKey, data);
//                    info[counter++] = packet.new MultiBlockChangeInfo(packet, entry.locationKey, data);
                }

                try {
                    infoArrayField.set(packet, info);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                short[] data = new short[changes.get(chunkKey).size()];
                for (Entry entry : changes.get(chunkKey)) {
                    data[counter++] = entry.locationKey;
                }
                packet = new PacketPlayOutMultiBlockChange(data.length, data, chunk);
            }

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public static class Entry {
        private final short locationKey;
        private final int blockType;

        public Entry(short locationKey, int blockType) {
            this.locationKey = locationKey;
            this.blockType = blockType;
        }
    }

}
