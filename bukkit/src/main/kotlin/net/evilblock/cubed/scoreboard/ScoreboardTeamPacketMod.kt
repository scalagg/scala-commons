/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.scoreboard

import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.cubed.util.nms.MinecraftReflection
import org.bukkit.entity.Player

class ScoreboardTeamPacketMod(teamName: String, prefix: String, suffix: String, players: MutableCollection<String>?, action: Int) {

    private val packet = PACKET_PLAY_OUT_SCOREBOARD_TEAM.newInstance()

    init {
        try {
            Reflection.setDeclaredFieldValue(packet, "a", teamName)
            Reflection.setDeclaredFieldValue(packet, "h", action)
            Reflection.setDeclaredFieldValue(packet, "f", -1)

            if (action == 0 || action == 2) {
                Reflection.setDeclaredFieldValue(packet, "b", teamName)
                Reflection.setDeclaredFieldValue(packet, "c", prefix)
                Reflection.setDeclaredFieldValue(packet, "d", suffix)
                Reflection.setDeclaredFieldValue(packet, "f", 3)
            }

            if (action == 0) {
                addAll(players)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    constructor(teamName: String, players: MutableCollection<String>, action: Int): this(teamName, "", "", players, action) {
        try {
            Reflection.setDeclaredFieldValue(packet, "a", teamName)
            Reflection.setDeclaredFieldValue(packet, "i", action)
            Reflection.setDeclaredFieldValue(packet, "f", 3)

            addAll( players)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun send(player: Player) {
        MinecraftProtocol.send(player, packet)
    }

    private fun addAll(col: MutableCollection<String>?) {
        if (col == null) {
            return
        }

        try {
            (Reflection.getDeclaredFieldValue(packet, "g") as MutableCollection<String>).addAll(col)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private val PACKET_PLAY_OUT_SCOREBOARD_TEAM = MinecraftProtocol
            .getPacketClass("PacketPlayOutScoreboardTeam")
    }

}
