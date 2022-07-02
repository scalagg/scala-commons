package gg.scala.commons;

import com.sk89q.worldedit.blocks.BlockType;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SignGUI implements Listener {

    public static void openSignEditor(Player player) {
        final PacketPlayOutOpenSignEditor packet =
            new PacketPlayOutOpenSignEditor(
                new BlockPosition(0, 0, 0)
            );


        ((CraftPlayer) player).getHandle()
            .playerConnection.sendPacket(packet);
    }

    public void registerSignUpdateListener(
        @Nonnull final Plugin plugin,
        @Nonnull final String worldName,
        @Nonnull final List<String> defaultLines
    ) {
        final World world = plugin
            .getServer().getWorld(worldName);

        if (world == null) {
            throw new IllegalArgumentException("World does not exist");
        }

        final org.bukkit.block.Block block =
            world.getBlockAt(0, 0, 0);
        block.setType(Material.SIGN_POST);

        block.getState().setType(Material.SIGN_POST);
        block.getState().update();

        final BlockState state =
            block.getState();

        if (!(state instanceof Sign)) {
            throw new IllegalArgumentException("Block at (0,0,0) is not a sign");
        }

        final Sign sign = (Sign) state;

        final AtomicInteger current =
            new AtomicInteger();

        defaultLines.forEach(line ->
            sign.setLine(current.getAndIncrement(), line)
        );

        sign.update();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final CraftPlayer craftPlayer =
            (CraftPlayer) event.getPlayer();

        craftPlayer.getHandle().playerConnection
            .networkManager.channel.pipeline()
            .addBefore("packet_handler", "sign_updates", new ChannelDuplexHandler() {
                @Override
                public void channelRead(
                    @Nonnull final ChannelHandlerContext channelHandlerContext,
                    @Nonnull final Object message
                ) throws Exception {
                    try {
                        if (message instanceof PacketPlayInUpdateSign) {
                            final PacketPlayInUpdateSign update =
                                (PacketPlayInUpdateSign) message;

                            final BlockPosition position = update.a();

                            if (position.getX() == 0 && position.getY() == 0 && position.getZ() == 0) {
                                final SignGUIEvent guiEvent = new SignGUIEvent(
                                    event.getPlayer().getUniqueId(),
                                    Arrays.stream(update.b())
                                        .map(IChatBaseComponent.ChatSerializer::a)
                                        .collect(Collectors.toList())
                                );

                                Bukkit.getPluginManager().callEvent(guiEvent);
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    super.channelRead(channelHandlerContext, message);
                }
            });
    }

    public static class SignGUIEvent extends Event {

        public final UUID player;
        public final List<String> lines;

        public SignGUIEvent(
            final @Nonnull UUID player,
            final @Nonnull List<String> lines
        ) {
            this.player = player;
            this.lines = lines;
        }

        public static final HandlerList handlerList = new HandlerList();

        public static HandlerList getHandlerList() {
            return handlerList;
        }

        @Override
        public HandlerList getHandlers() {
            return handlerList;
        }
    }
}
