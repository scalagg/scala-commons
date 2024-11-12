package net.evilblock.cubed.nametag

import net.evilblock.cubed.scoreboard.ScoreboardTeamPacketMod
import java.util.ArrayList

class NametagInfo(
    val name: String,
    val prefix: String,
    val suffix: String
)
{
    val teamAddPacket =
        ScoreboardTeamPacketMod(name, prefix, suffix, ArrayList(), 0)
}
