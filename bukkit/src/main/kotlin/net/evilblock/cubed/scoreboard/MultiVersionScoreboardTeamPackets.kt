package net.evilblock.cubed.scoreboard

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

class MultiVersionScoreboardTeamPackets(
    val teamID: String,
    val prefix: String = "",
    val suffix: String = "",
    optionData: WrapperPlayServerTeams.OptionData = WrapperPlayServerTeams.OptionData.ALL,
    collisionRule: WrapperPlayServerTeams.CollisionRule = WrapperPlayServerTeams.CollisionRule.ALWAYS,
    nameTagVisibility: WrapperPlayServerTeams.NameTagVisibility = WrapperPlayServerTeams.NameTagVisibility.ALWAYS
)
{
    private val serializer = LegacyComponentSerializer.legacySection().toBuilder()
        .useUnusualXRepeatedCharacterHexFormat()
        .build()

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

        if ("§x" in input)
        {
            val hexMatches = Regex("§x(§[0-9a-fA-F]){6}").findAll(input).toList()
            val lastHex = hexMatches.lastOrNull()?.value
                ?: return null
            val normalized = lastHex.removePrefix("§x").replace("§", "")
            if (normalized.length == 6)
            {
                val red = normalized.substring(0, 2).toInt(16)
                val green = normalized.substring(2, 4).toInt(16)
                val blue = normalized.substring(4, 6).toInt(16)
                return nearestNamedColor(red, green, blue)
            }

            return null
        }

        return lastColorCode?.let { colorCodeMapping[it] }
    }

    private fun nearestNamedColor(red: Int, green: Int, blue: Int): NamedTextColor
    {
        val palette = mapOf(
            NamedTextColor.BLACK to intArrayOf(0, 0, 0),
            NamedTextColor.DARK_BLUE to intArrayOf(0, 0, 170),
            NamedTextColor.DARK_GREEN to intArrayOf(0, 170, 0),
            NamedTextColor.DARK_AQUA to intArrayOf(0, 170, 170),
            NamedTextColor.DARK_RED to intArrayOf(170, 0, 0),
            NamedTextColor.DARK_PURPLE to intArrayOf(170, 0, 170),
            NamedTextColor.GOLD to intArrayOf(255, 170, 0),
            NamedTextColor.GRAY to intArrayOf(170, 170, 170),
            NamedTextColor.DARK_GRAY to intArrayOf(85, 85, 85),
            NamedTextColor.BLUE to intArrayOf(85, 85, 255),
            NamedTextColor.GREEN to intArrayOf(85, 255, 85),
            NamedTextColor.AQUA to intArrayOf(85, 255, 255),
            NamedTextColor.RED to intArrayOf(255, 85, 85),
            NamedTextColor.LIGHT_PURPLE to intArrayOf(255, 85, 255),
            NamedTextColor.YELLOW to intArrayOf(255, 255, 85),
            NamedTextColor.WHITE to intArrayOf(255, 255, 255)
        )

        return palette.minByOrNull { (_, color) ->
            val redDiff = red - color[0]
            val greenDiff = green - color[1]
            val blueDiff = blue - color[2]
            redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff
        }?.key ?: NamedTextColor.WHITE
    }

    private val teamInfo = WrapperPlayServerTeams.ScoreBoardTeamInfo(
        Component.text("test"),
        serializer.deserialize(prefix),
        serializer.deserialize(suffix),
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
