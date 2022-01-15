package gg.scala.commons.entity.hologram.patch;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.ScheduledPacket;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import us.myles.ViaVersion.ViaVersionPlugin;
import us.myles.ViaVersion.api.ViaAPI;

import java.util.Iterator;
import java.util.List;

/**
 * @author GrowlyX
 * @since 1/4/2022
 */
public class EntityHologramPatch extends PacketAdapter {
    private static final Byte ENTITY_INVISIBLE = 32;

    private final ViaAPI<Player> api;

    public EntityHologramPatch(JavaPlugin plugin) {
        super(plugin, ListenerPriority.HIGHEST,
                PacketType.Play.Server.SPAWN_ENTITY_LIVING, PacketType.Play.Server.SPAWN_ENTITY,
                PacketType.Play.Server.ATTACH_ENTITY, PacketType.Play.Server.ENTITY_METADATA,
                PacketType.Play.Server.ENTITY_TELEPORT
        );

        api = ViaVersionPlugin
                .getPlugin(ViaVersionPlugin.class)
                .getApi();
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (api.getPlayerVersion(event.getPlayer()) > 5) {
            if (!event.isCancelled()) {
                Entity entity;
                List<WrappedWatchableObject> metadata;
                if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
                    WrapperPlayServerSpawnEntityLiving spawnLivingPacket = new WrapperPlayServerSpawnEntityLiving(event.getPacket());
                    entity = spawnLivingPacket.getEntity(event);
                    if (entity == null) {
                        return;
                    }

                    if (spawnLivingPacket.getType() == EntityType.HORSE) {
                        spawnLivingPacket.setType(EntityType.fromId(30));
                        metadata = spawnLivingPacket.getMetadata().getWatchableObjects();
                        if (metadata != null) {
                            this.fixIndexes(metadata, event.getPlayer());
                            metadata.add(new WrappedWatchableObject(0, ENTITY_INVISIBLE));
                            spawnLivingPacket.setMetadata(new WrappedDataWatcher(metadata));
                        }
                    }
                } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
                    WrapperPlayServerSpawnEntity spawnPacket = new WrapperPlayServerSpawnEntity(event.getPacket());
                    entity = spawnPacket.getEntity(event);
                    if (entity == null) {
                        return;
                    }

                    if (spawnPacket.getType() == 66) {
                        spawnPacket.setType(78);
                        WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata();
                        metadataPacket.setEntityID(spawnPacket.getEntityID());
                        List<WrappedWatchableObject> metadata2 = metadataPacket.getMetadata();
                        metadata2.add(new WrappedWatchableObject(0, ENTITY_INVISIBLE));
                        metadataPacket.setMetadata(metadata2);
                        event.schedule(ScheduledPacket.fromSilent(metadataPacket.getHandle(), event.getPlayer()));
                    }
                } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
                    WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata(event.getPacket());
                    entity = metadataPacket.getEntity(event);
                    if (entity == null) {
                        return;
                    }

                    if (entity.getType() == EntityType.HORSE) {
                        metadata = metadataPacket.getMetadata();
                        this.fixIndexes(metadata, event.getPlayer());
                        metadata.add(new WrappedWatchableObject(0, ENTITY_INVISIBLE));
                        metadataPacket.setMetadata(metadata);
                    }
                } else {
                    Entity passenger;
                    if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
                        WrapperPlayServerEntityTeleport teleportPacket = new WrapperPlayServerEntityTeleport(event.getPacket());
                        entity = teleportPacket.getEntity(event);
                        if (entity == null) {
                            return;
                        }

                        if (entity.getType() == EntityType.WITHER_SKULL) {
                            passenger = entity.getPassenger();
                            if (passenger == null) {
                                return;
                            }

                            if (passenger.getType() != EntityType.DROPPED_ITEM && passenger.getType() != EntityType.SLIME) {
                                if (passenger.getType() == EntityType.HORSE) {
                                    teleportPacket.setEntityID(entity.getPassenger().getEntityId());
                                    teleportPacket.setY(entity.getLocation().getY() - 58.25D);
                                }
                            } else {
                                teleportPacket.setY(entity.getLocation().getY() - 1.2D);
                            }
                        }
                    }
                }

            }
        }
    }

    private void fixIndexes(List<WrappedWatchableObject> metadata, Player player) {
        Iterator<WrappedWatchableObject> iter = metadata.iterator();

        while (true) {
            WrappedWatchableObject current;
            String customName;
            do {
                do {
                    label25:
                    do {
                        while (iter.hasNext()) {
                            current = iter.next();
                            if (current.getIndex() == 2) {
                                continue label25;
                            }

                            if (current.getIndex() != 3) {
                                iter.remove();
                            }
                        }

                        return;
                    } while (current.getValue() == null);
                } while (current.getValue().getClass() != String.class);

                customName = (String) current.getValue();
            } while (!customName.contains("{player}") && !customName.contains("{displayname}"));

            customName = customName.replace("{player}", player.getName()).replace("{displayname}", player.getDisplayName());
            current.setValue(customName);
        }
    }
}
