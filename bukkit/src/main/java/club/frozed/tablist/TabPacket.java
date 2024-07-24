package club.frozed.tablist;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.plugin.Plugin;

/**
 * Created by Ryzeon
 * Project: TabAPI
 * Date: 11/12/2020 @ 09:58
 */

public class TabPacket extends PacketAdapter {
    public TabPacket(Plugin plugin) {
        super(plugin, PacketType.Play.Server.SCOREBOARD_TEAM);
    }

    private static final PacketType SCOREBOARD_TEAM_PACKET =
        PacketType.fromClass(PacketPlayOutScoreboardTeam.class);

    @Override
    public void onPacketSending(PacketEvent event) {
        final var scoreboardTeamThing = new WrapperPlayServerScoreboardTeam(event.getPacket());

        System.out.println(scoreboardTeamThing.getName());

        if (scoreboardTeamThing.getMode() == 4 && scoreboardTeamThing.getName().equalsIgnoreCase("tab")) {
            scoreboardTeamThing.setName("tab");
            scoreboardTeamThing.setNameTagVisibility("tab");
            scoreboardTeamThing.setMode(3);

//            event.setPacket(scoreboardTeamThing.getHandle());
        }
    }
}
