package net.evilblock.cubed.nametag

import net.evilblock.cubed.scoreboard.MultiVersionScoreboardTeamPackets

class NametagInfo(
    val name: String,
    val prefix: String,
    val suffix: String,
    val priority: Int = 0
)
{
    val teamAddPacket =
        MultiVersionScoreboardTeamPackets(name, prefix, suffix)
            .newCreatePacket(listOf())
}
