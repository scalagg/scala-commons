/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.scoreboard

import com.github.retrooper.packetevents.protocol.score.ScoreFormatTypes
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore
import com.google.common.base.Preconditions
import com.google.common.collect.ImmutableSet
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.scoreboard.preview.ScoreboardPreviewHandler
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.ServerVersion
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.cubed.util.nms.MinecraftReflection
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import java.util.*
import kotlin.collections.set

class Scoreboard(
    private val player: Player,
    private val plugin: ScalaCommonsSpigot
)
{
    private val objective: Objective
    private val displayedScores: HashMap<String, Int> = HashMap()
    private val scorePrefixes: HashMap<String, String> = HashMap()
    private val scoreSuffixes: HashMap<String, String> = HashMap()
    private val sentTeamCreates: HashSet<String> = HashSet()
    private val separateScoreBuilder: StringBuilder = StringBuilder()
    private val separateScores: ArrayList<String> = ArrayList()
    private val recentlyUpdatedScores: HashSet<String> = HashSet()
    private val usedBaseScores: HashSet<String> = HashSet()
    private val prefixScoreSuffix: Array<String> = arrayOf("", "", "")
    private val afterV113 = MinecraftProtocol.getPlayerVersion(player) > 756
    private val localList: ThreadLocal<LinkedList<String>> = ThreadLocal.withInitial { LinkedList<String>() }

    init
    {
        val scoreboard = if (player.scoreboard == Bukkit.getScoreboardManager().mainScoreboard)
        {
            Bukkit.getScoreboardManager().newScoreboard
        } else
        {
            player.scoreboard
        }

        if (scoreboard.getObjective(DisplaySlot.SIDEBAR) != null)
        {
            objective = scoreboard.getObjective(DisplaySlot.SIDEBAR)
        } else
        {
            objective = scoreboard.registerNewObjective("commons", "dummy")
            objective.displaySlot = DisplaySlot.SIDEBAR
        }

        if (player.scoreboard != scoreboard)
        {
            player.scoreboard = scoreboard
        }
    }

    fun update()
    {
        val override = ScoreboardHandler.scoreboardOverride
        val shouldOverride = override != null && override.shouldOverride(this.player)

        val title = ChatColor.translateAlternateColorCodes(
            '&',
            if (shouldOverride) override!!.getTitle(player) else ScoreboardHandler.scoreboardProvider!!.getTitle(player)
        )

        val lines = localList.get()
        if (!lines.isEmpty())
        {
            lines.clear()
        }

        ScoreboardHandler.scoreboardProvider!!.preRun(player, this.objective.scoreboard)

        if (ScoreboardPreviewHandler.previewGlobal || ScoreboardPreviewHandler.previewMode.contains(player.uniqueId))
        {
            this.localList.get().addAll(ScoreboardPreviewHandler.lines)
        } else
        {
            if (shouldOverride)
            {
                override!!.getLines(this.localList.get(), this.player)
            } else
            {
                ScoreboardHandler.scoreboardProvider!!.getLines(this.localList.get(), this.player)
            }
        }

        if (localList.get().isEmpty())
        {
            return
        }

        this.recentlyUpdatedScores.clear()
        this.usedBaseScores.clear()

        var nextValue = lines.size
        Preconditions.checkArgument(lines.size <= 15, "Too many lines passed! (${lines.size})" as Any)
        Preconditions.checkArgument(title.length <= 32, "Title is too long! (${title.length})" as Any)

        if (this.objective.displayName != title)
        {
            this.objective.displayName = title
        }

        for (s in lines)
        {
            val nextValues = ChatColor
                .values()[nextValue]
                .toString()

            val line = "$nextValues${CC.WHITE}$s"

            if (!this.afterV113 && line.length > 48)
            {
                throw IllegalArgumentException(
                    "Line is too long! Offending line: ${
                        line.replace(
                            ChatColor.COLOR_CHAR,
                            '&'
                        )
                    }"
                )
            }

            val separated = this
                .separate(line, this.usedBaseScores)

            var prefix =  separated[0]
            var score = separated[1]
            var suffix = separated[2]

            this.recentlyUpdatedScores.add(score)

            if (!this.sentTeamCreates.contains(score))
            {
                this.createAndAddMember(score)
            }

            if (
                !this.displayedScores.containsKey(score) ||
                this.displayedScores[score] != nextValue
            )
            {
                this.setScore(score, nextValue)
            }

            if (
                !this.scorePrefixes.containsKey(score) ||
                !this.scorePrefixes[score].equals(prefix) ||
                !this.scoreSuffixes[score].equals(suffix)
            )
            {
                this.updateScore(score, prefix, suffix)
            }

            --nextValue
        }

        for (displayedScore in ImmutableSet.copyOf(this.displayedScores.keys))
        {
            if (this.recentlyUpdatedScores.contains(displayedScore))
            {
                continue
            }

            this.removeScore(displayedScore)
        }
    }

    private fun createAndAddMember(score: String)
    {
        val interactions = MultiVersionScoreboardTeamPackets(teamID = score, prefix = "_", suffix = "_")
        interactions
            .newCreatePacket(listOf())
            .send(this.player)

        interactions
            .newTeamMemberUpdatePacket(
                score,
                WrapperPlayServerTeams.TeamMode.ADD_ENTITIES
            )
            .send(player)

        this.sentTeamCreates.add(score)
    }

    fun scoreboardScore(score: String, value: Int, scoreboardAction: WrapperPlayServerUpdateScore.Action, objectiveName: String = objective.name) =
        WrapperPlayServerUpdateScore(score, scoreboardAction, objectiveName, Optional.of(value))

    private fun setScore(score: String, value: Int)
    {
        if (ServerVersion.getVersion().isNewerThan(ServerVersion.v1_8_8))
        {
            this.displayedScores[score] = value
            scoreboardScore(score, value, WrapperPlayServerUpdateScore.Action.CREATE_OR_UPDATE_ITEM)
                .send(player)
            return
        }

        val packet = MinecraftProtocol.newPacket("PacketPlayOutScoreboardScore")
        Reflection.setDeclaredFieldValue(packet, "a", score)
        Reflection.setDeclaredFieldValue(packet, "b", this.objective.name)
        Reflection.setDeclaredFieldValue(packet, "c", value)

        val enumType = MinecraftReflection.getNMSClass("PacketPlayOutScoreboardScore\$EnumScoreboardAction")!!
        Reflection.setDeclaredFieldValue(packet, "d", Reflection.getEnum(enumType, "CHANGE")!!)

        this.displayedScores[score] = value
        MinecraftProtocol.send(player, packet)
    }

    private fun removeScore(score: String)
    {
        this.displayedScores.remove(score)
        this.scorePrefixes.remove(score)
        this.scoreSuffixes.remove(score)

        if (ServerVersion.getVersion().isNewerThan(ServerVersion.v1_8_8))
        {
            scoreboardScore(score, 0, WrapperPlayServerUpdateScore.Action.REMOVE_ITEM, "")
                .send(player)
            return
        }

        val packet = MinecraftProtocol.newPacket("PacketPlayOutScoreboardScore")
        Reflection.setDeclaredFieldValue(packet, "a", score)
        Reflection.setDeclaredFieldValue(packet, "b", "")
        Reflection.setDeclaredFieldValue(packet, "c", 0)

        val enumType = MinecraftReflection.getNMSClass("PacketPlayOutScoreboardScore\$EnumScoreboardAction")!!
        val enum = Reflection.getEnum(enumType, "REMOVE")!!
        Reflection.setDeclaredFieldValue(packet, "d", enum)

        MinecraftProtocol.send(player, packet)
    }

    private fun updateScore(
        score: String, prefix: String, suffix: String
    )
    {
        this.scorePrefixes[score] = prefix
        this.scoreSuffixes[score] = suffix

        val interactions = MultiVersionScoreboardTeamPackets(teamID = score, prefix = prefix, suffix = suffix)
        interactions
            .newUpdatePacket()
            .send(this.player)
    }

    private fun separate(line: String, usedBaseScores: MutableCollection<String>): Array<String>
    {
        var line = ChatColor
            .translateAlternateColorCodes('&', line)
        var prefix = ""
        var score = ""
        var suffix = ""

        this.separateScores.clear()
        this.separateScoreBuilder.setLength(0)

        for (element in line)
        {
            if (element == '*' || this.separateScoreBuilder.length == (if (this.afterV113) 512 else 16) && this.separateScores.size < 3)
            {
                this.separateScores.add(this.separateScoreBuilder.toString())
                this.separateScoreBuilder.setLength(0)
                if (element == '*')
                {
                    continue
                }
            }
            this.separateScoreBuilder.append(element)
        }

        this.separateScores.add(this.separateScoreBuilder.toString())

        when (this.separateScores.size)
        {
            1 ->
            {
                score = this.separateScores[0]
            }
            2 ->
            {
                if (this.afterV113)
                {
                    prefix = this.separateScores[1]
                    score = this.separateScores[0]
                } else
                {
                    score = this.separateScores[0]
                    suffix = this.separateScores[1]
                }
            }
            3 ->
            {
                prefix = this.separateScores[0]
                score = this.separateScores[1]
                suffix = this.separateScores[2]
            }
            else ->
            {
                plugin.logger.warning("Failed to separate scoreboard line. Input: $line")
            }
        }

        if (usedBaseScores.contains(score))
        {
            if (score.length <= (if (this.afterV113) 514 else 14))
            {
                for (chatColor in ChatColor.values())
                {
                    val possibleScore = chatColor.toString() + score
                    if (!usedBaseScores.contains(possibleScore))
                    {
                        score = possibleScore
                        break
                    }
                }
            }
        }

        if (prefix.length > (if (this.afterV113) 512 else 16))
        {
            prefix = "${ChatColor.DARK_RED}${ChatColor.BOLD}>16"
        }

        if (score.length > (if (this.afterV113) 512 else 16))
        {
            score = "${ChatColor.DARK_RED}${ChatColor.BOLD}>16"
        }

        if (suffix.length > (if (this.afterV113) 512 else 16))
        {
            suffix = "${ChatColor.DARK_RED}${ChatColor.BOLD}>16"
        }

        usedBaseScores.add(score)

        this.prefixScoreSuffix[0] = prefix
        this.prefixScoreSuffix[1] = score
        this.prefixScoreSuffix[2] = suffix

        return this.prefixScoreSuffix
    }
}
