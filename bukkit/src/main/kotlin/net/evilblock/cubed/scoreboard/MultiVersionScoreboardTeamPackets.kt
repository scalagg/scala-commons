package net.evilblock.cubed.scoreboard

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.InternalStructure
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.wrappers.WrappedChatComponent
import me.lucko.helper.modern.PacketScoreboard
import me.lucko.helper.modern.ScoreboardTeam
import org.bukkit.ChatColor
import java.util.*

/**
 * @author GrowlyX
 * @since 10/7/2024
 */
class MultiVersionScoreboardTeamPackets(
    val teamID: String,
    val prefix: String = "",
    val suffix: String = "",
    val friendlyFire: Boolean = true,
    val seeFriendlyInvisibles: Boolean = true,
    val collisionRule: ScoreboardTeam.CollisionRule = ScoreboardTeam.CollisionRule.ALWAYS,
    val nameTagVisibility: ScoreboardTeam.NameTagVisibility = ScoreboardTeam.NameTagVisibility.ALWAYS
)
{
    fun newCreatePacket(players: List<String>): PacketContainer
    {
        // create an update packet (as that contains a number of values required by the create packet)
        val packet = newUpdatePacket()

        // set mode - byte
        packet.integers.write(0, UpdateType.CREATE.code)

        // add player info - array of String(40)
        // set players - ProtocolLib handles setting 'Entity Count'
        packet.getSpecificModifier(MutableCollection::class.java)
            .write(0, players.toMutableList())

        return packet
    }

    fun newRemovePacket(): PacketContainer
    {
        // http://wiki.vg/Protocol#Teams
        val packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM)

        // remove packet only has the default fields

        // set team name - 	String (16)
        packet.strings.write(0, teamID)

        // set mode - byte
        packet.integers.write(0, UpdateType.REMOVE.code)

        return packet
    }

    fun newUpdatePacket(): PacketContainer
    {
        // http://wiki.vg/Protocol#Teams
        val packetUpdate =
            ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_TEAM)
        packetUpdate.strings.write(0, teamID)
        packetUpdate.strings.writeSafely(2, prefix)
        packetUpdate.strings.writeSafely(3, suffix)
        packetUpdate.chatComponents.writeSafely(1, WrappedChatComponent.fromText(prefix))
        packetUpdate.chatComponents.writeSafely(2, WrappedChatComponent.fromText(suffix))
        packetUpdate.integers.write(if (packetUpdate.integers.size() > 1) 1 else 0, 2)
        if (packetUpdate.optionalStructures.size() > 0) {
            packetUpdate.optionalStructures.read(0).get().let {
                it.chatComponents.writeSafely(1, WrappedChatComponent.fromText(prefix))
                it.chatComponents.writeSafely(2, WrappedChatComponent.fromText(suffix))
                packetUpdate.optionalStructures.writeSafely(0, Optional.of(it))
            }
        }

/*        val packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM)
        packet.modifier.writeDefaults()

        // set team name - 	String (16)
        packet.strings.write(0, teamID)

        // set mode - byte
        packet.integers.write(0, UpdateType.UPDATE.code)

        val struct = packet.optionalStructures.readSafely(0).get()
        println(packet.optionalStructures.toString())

        // set display name - Component
        struct.chatComponents.write(0, PacketScoreboard.toComponent(teamID))

        // set prefix - Component
        struct.chatComponents.write(1, PacketScoreboard.toComponent(prefix))

        // set suffix - Component
        struct.chatComponents.write(2, PacketScoreboard.toComponent(suffix))

        // friendly flags - byte - Bit mask. 0x01: Allow friendly fire, 0x02: can see invisible entities on same team
        var flags = 0
        if (friendlyFire)
        {
            flags = flags or 1
        }
        if (seeFriendlyInvisibles)
        {
            flags = flags or 2
        }

        // set flags
        struct.integers.write(1, flags)

        // set nametag visibility - String Enum (32)
        struct.strings.write(0, nameTagVisibility.protocolName)

        // set collision rule - String Enum (32)
        struct.strings.write(0, collisionRule.protocolName)

        // set color - byte - For colors, the same Chat colors (0-15). -1 indicates RESET/no color.
        struct.getEnumModifier(
            ChatColor::class.java,
            MinecraftReflection.getMinecraftClass("EnumChatFormat")
        ).write(0, ChatColor.RESET)

        packet.optionalStructures.write(0, Optional.of(struct))*/

        return packetUpdate
    }

    fun newTeamMemberUpdatePacket(player: String, action: MemberAction): PacketContainer
    {
        // http://wiki.vg/Protocol#Teams
        val packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM)

        // set team name - 	String (16)
        packet.strings.write(0, teamID)

        // set mode
        when (action)
        {
            MemberAction.ADD -> packet.integers.write(
                0,
                UpdateType.ADD_PLAYERS.code
            )

            MemberAction.REMOVE -> packet.integers.write(
                0,
                UpdateType.REMOVE_PLAYERS.code
            )

            else -> throw RuntimeException()
        }

        // set players - Array of String (40)
        packet.getSpecificModifier(MutableCollection::class.java)
            .write(0, mutableListOf(player))

        return packet
    }

    enum class MemberAction
    {
        ADD, REMOVE
    }

    enum class UpdateType(val code: Int)
    {
        CREATE(0),
        REMOVE(1),
        UPDATE(2),
        ADD_PLAYERS(3),
        REMOVE_PLAYERS(4)
    }
}
