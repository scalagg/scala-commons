package gg.scala.commons.nametag

import me.lucko.helper.scoreboard.PacketScoreboardTeam
import net.kyori.adventure.text.Component
import java.util.*

/**
 * @author GrowlyX
 * @since 4/2/2024
 */
data class NametagSpec(
    val prefix: Component,
    val suffix: Component,
    val team: PacketScoreboardTeam,
    val uniqueId: UUID = UUID.randomUUID()
)
