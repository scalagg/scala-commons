

package net.evilblock.cubed.nametag.update

import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.nms.MinecraftProtocol
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.NameTagVisibility
import kotlin.random.Random

class NametagUpdates(
    var prefix: String = "",
    var name: String = "",
    var suffix: String = "",
    var invisible: Boolean = true,
    var priority: Int = 1
) {

    init {
        assert(prefix.length <= 16) { "Prefix is too long! (${prefix.length} > 16)" }
        assert(name.length <= 16) { "Name is too long! (${name.length} > 16)" }
        assert(suffix.length <= 16) { "Suffix is too long! (${suffix.length} > 16)" }
    }

    constructor(text: String = "", invisible: Boolean, priority: Int) : this("", "", "", invisible, priority) {
        if (text.isEmpty() && invisible) {
            name = generateName()
            return
        }

        assert(text.length in 0..48) { "Text is too long! (${text.length} > 48})" }

        if (text.length <= 16) {
            this.name = text
        } else {
            if (text.length <= 32) {
                this.prefix = text.substring(0, text.length - 16)

                if (this.prefix.endsWith(ChatColor.COLOR_CHAR)) {
                    this.prefix = "${this.prefix}${text.toCharArray()[text.length - 16]}"
                    this.name = text.substring(text.length - 15, text.length)
                } else {
                    this.name = text.substring(text.length - 16, text.length)
                }
            } else {
                this.prefix = text.substring(0, 15)
                this.name = text.substring(15, 31)
                this.suffix = text.substring(31, text.length)
            }
        }
    }

    private fun hasScoreboard(player: Player): Boolean {
        return player.scoreboard != null && player.scoreboard.getObjective(DisplaySlot.PLAYER_LIST) != null
    }

    private fun createScoreboard(player: Player) {
        val scoreboard = if (player.scoreboard == Bukkit.getScoreboardManager().mainScoreboard) {
            Bukkit.getScoreboardManager().newScoreboard
        } else {
            player.scoreboard
        }

        if (
            !this.invisible
        )
        {
            if (scoreboard.getObjective(DisplaySlot.PLAYER_LIST) == null) {
                kotlin.runCatching {
                    val objective = scoreboard
                        .registerNewObjective(
                            "NametagUpdates", "dummy"
                        )
                    objective.displaySlot = DisplaySlot.PLAYER_LIST
                }
            }
        }

        if (player.scoreboard != scoreboard) {
            player.scoreboard = scoreboard
        }

        player.setMetadata("NAMETAG_UPDATES_SB", FixedMetadataValue(ScalaCommonsSpigot.instance, scoreboard))
    }

    fun enforceSendSync(player: Player, team: String) {
        if (Bukkit.isPrimaryThread()) {
            send(player, team)
        } else {
            Tasks.sync {
                send(player, team)
            }
        }
    }

    fun priorityAsStr() = if (this.priority < 10 && this.priority != -1) {
        "0${this.priority}"
    } else {
        if (this.priority == -1)
            "\u00A70\u00A73\u00A71\u00A79" else this.priority.toString()
    }

    fun send(player: Player, team: String) {
        if (!hasScoreboard(player)) {
            createScoreboard(player)
        }

        val teamName = getTeamName(priorityAsStr() + team)
        val scoreboard = player.scoreboard
        val scoreboardTeam = scoreboard.getTeam(teamName) ?: scoreboard.registerNewTeam(teamName)

        if (invisible) {
            scoreboardTeam.nameTagVisibility = NameTagVisibility.NEVER
        } else {
            scoreboardTeam.displayName = this.name
            scoreboardTeam.prefix = this.prefix
            scoreboardTeam.suffix = this.suffix
        }

        scoreboardTeam.addEntry(this.name)
    }

    fun enforceClearSync(player: Player, team: String) {
        if (Bukkit.isPrimaryThread()) {
            clear(player, team)
        } else {
            Tasks.sync {
                clear(player, team)
            }
        }
    }

    fun clear(player: Player, team: String) {
        if (hasScoreboard(player)) {
            val teamName = getTeamName(priorityAsStr() + team)

            val scoreboardTeam = player.scoreboard.getTeam(teamName)
            if (scoreboardTeam != null) {
                val entries = scoreboardTeam.entries
                for (entry in entries) {
                    scoreboardTeam.removeEntry(entry)
                }

                scoreboardTeam.unregister()
            }
        }
    }

    private fun getTeamName(team: String): String {
        return if (team.length > 16) {
            team.substring(0, 12)
        } else {
            team
        }
    }

    companion object {
        private val chars = arrayListOf('a', 'b', 'c', 'd', 'e', 'f', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

        private fun generateName(): String {
            val amountOfColors = Random.nextInt(1, 8)
            var name = ""
            for (i in 1..amountOfColors) {
                name = "${ChatColor.COLOR_CHAR}${chars[Random.nextInt(chars.size)]}"
            }
            return name
        }
    }

}
