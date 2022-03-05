package net.evilblock.cubed.nametag

import net.evilblock.cubed.scoreboard.ScoreboardTeamPacketMod
import java.util.ArrayList

class NametagInfo constructor(val name: String, val prefix: String, val suffix: String) {

    val teamAddPacket = ScoreboardTeamPacketMod(name, prefix, suffix, ArrayList(), 0)

    override fun equals(other: Any?): Boolean {
        if (other is NametagInfo) {
            val otherNametag = other as NametagInfo?
            return this.name == otherNametag!!.name && this.prefix == otherNametag.prefix && this.suffix == otherNametag.suffix
        }
        return false
    }

    override fun hashCode(): Int
    {
        var result = name.hashCode()
        result = 31 * result + prefix.hashCode()
        result = 31 * result + suffix.hashCode()
        result = 31 * result + teamAddPacket.hashCode()
        return result
    }
}
