package net.evilblock.cubed.scoreboard

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

/**
 * @author GrowlyX
 * @since 10/7/2024
 */
class MultiVersionScoreboardTeamPackets(
    val teamID: String,
    val prefix: String = "",
    val suffix: String = "",
    optionData: WrapperPlayServerTeams.OptionData = WrapperPlayServerTeams.OptionData.ALL,
    collisionRule: WrapperPlayServerTeams.CollisionRule = WrapperPlayServerTeams.CollisionRule.ALWAYS,
    nameTagVisibility: WrapperPlayServerTeams.NameTagVisibility = WrapperPlayServerTeams.NameTagVisibility.ALWAYS
)
{
    fun getLastChatColor(input: String): NamedTextColor?
    {
        val colorCodeMapping = mapOf(
            "§0" to NamedTextColor.BLACK,
            "§1" to NamedTextColor.DARK_BLUE,
            "§2" to NamedTextColor.DARK_GREEN,
            "§3" to NamedTextColor.DARK_AQUA,
            "§4" to NamedTextColor.DARK_RED,
            "§5" to NamedTextColor.DARK_PURPLE,
            "§6" to NamedTextColor.GOLD,
            "§7" to NamedTextColor.GRAY,
            "§8" to NamedTextColor.DARK_GRAY,
            "§9" to NamedTextColor.BLUE,
            "§a" to NamedTextColor.GREEN,
            "§b" to NamedTextColor.AQUA,
            "§c" to NamedTextColor.RED,
            "§d" to NamedTextColor.LIGHT_PURPLE,
            "§e" to NamedTextColor.YELLOW,
            "§f" to NamedTextColor.WHITE
        )

        val regex = Regex("§[0-9a-fA-F]")
        val lastColorCode = regex.findAll(input).lastOrNull()?.value

        return lastColorCode?.let { colorCodeMapping[it] }
    }

    private val teamInfo = WrapperPlayServerTeams.ScoreBoardTeamInfo(
        Component.text("test"),
        LegacyComponentSerializer.legacySection().deserialize(prefix),
        LegacyComponentSerializer.legacySection().deserialize(suffix),
        nameTagVisibility,
        collisionRule,
        getLastChatColor(prefix),
        optionData
    )

    fun newCreatePacket(players: List<String>): WrapperPlayServerTeams
    {
        return WrapperPlayServerTeams(teamID, WrapperPlayServerTeams.TeamMode.CREATE, teamInfo, players)
    }

    fun newRemovePacket(): WrapperPlayServerTeams
    {
        return WrapperPlayServerTeams(teamID, WrapperPlayServerTeams.TeamMode.REMOVE, teamInfo)
    }

    fun newUpdatePacket(): WrapperPlayServerTeams
    {
        return WrapperPlayServerTeams(teamID, WrapperPlayServerTeams.TeamMode.UPDATE, teamInfo)
    }

    fun newTeamMemberUpdatePacket(player: String, teamMode: WrapperPlayServerTeams.TeamMode): WrapperPlayServerTeams
    {
        return WrapperPlayServerTeams(teamID, teamMode, teamInfo, player)
    }

}
