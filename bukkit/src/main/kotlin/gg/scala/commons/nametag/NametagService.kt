package gg.scala.commons.nametag

import com.googlecode.cqengine.ConcurrentIndexedCollection
import com.googlecode.cqengine.attribute.support.FunctionalSimpleAttribute
import com.googlecode.cqengine.index.hash.HashIndex
import com.googlecode.cqengine.index.unique.UniqueIndex
import com.googlecode.cqengine.query.QueryFactory
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import me.lucko.helper.Events
import me.lucko.helper.scoreboard.PacketScoreboardProvider
import me.lucko.helper.scoreboard.PacketScoreboardTeam
import me.lucko.helper.scoreboard.ScoreboardTeam
import me.lucko.helper.scoreboard.TeamNameGenerator
import net.evilblock.cubed.ScalaCommonsSpigot
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KProperty1

/**
 * @author GrowlyX
 * @since 4/2/2024
 */
@Service
@IgnoreAutoScan
object NametagService
{
    private val scoreboardProvider by lazy {
        PacketScoreboardProvider(ScalaCommonsSpigot.instance)
    }

    private val nameTagTeams = ConcurrentIndexedCollection<NametagSpec>()

    private val idAttribute = propertyAttribute(NametagSpec::uniqueId)
    private val prefixAttribute = propertyAttribute(NametagSpec::prefix)
    private val suffixAttribute = propertyAttribute(NametagSpec::suffix)

    private val playerToSpecMappings = ConcurrentHashMap<UUID, UUID>()

    @Configure
    fun configure()
    {
        nameTagTeams.addIndex(UniqueIndex.onAttribute(idAttribute))
        nameTagTeams.addIndex(HashIndex.onAttribute(prefixAttribute))
        nameTagTeams.addIndex(HashIndex.onAttribute(suffixAttribute))

        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                removeFromExisting(it.player)
                playerToSpecMappings.remove(it.player.uniqueId)
            }
            .bindWith(ScalaCommonsSpigot.instance)

        scoreboardProvider.scoreboard
    }

    private fun findExistingTeam(prefix: Component, suffix: Component) = nameTagTeams
        .retrieve(
            QueryFactory.and(
                QueryFactory.equal(prefixAttribute, prefix),
                QueryFactory.equal(suffixAttribute, suffix)
            )
        )
        .firstOrNull()

    private fun findTeamByID(id: UUID) = nameTagTeams
        .retrieve(QueryFactory.equal(idAttribute, id))
        .firstOrNull()

    private fun findTeamOfPlayer(player: Player): NametagSpec?
    {
        val spec = playerToSpecMappings[player.uniqueId]
            ?: return null

        return findTeamByID(spec)
    }

    private fun buildNewSpec(
        prefix: Component, suffix: Component, color: ChatColor?,
        sortPriority: Int, playerTag: Boolean = false
    ): NametagSpec
    {
        val generatedName = TeamNameGenerator.generate(sortPriority, playerTag)
        val team = PacketScoreboardTeam(generatedName, "", true)
        val newColor = color ?: prefix.color()?.value()?.let { closestChatColor(it) }
        team.apply {
            this.prefix = prefix
            this.suffix = suffix
            newColor?.apply context@{ this@apply.color = this }

            this.nameTagVisibility = ScoreboardTeam.NameTagVisibility.ALWAYS
        }

        scoreboardProvider.scoreboard.registerCustomTeam(generatedName, team)

        return NametagSpec(
            prefix = prefix, suffix = suffix, team = team
        ).apply(nameTagTeams::add)
    }

    fun setNametagOfPlayer(
        player: Player, prefix: Component, suffix: Component, color: ChatColor? = null,
        sortPriority: Int = 0,
        playerTag: Boolean = false
    )
    {
        val team = findExistingTeam(prefix, suffix)
            ?: buildNewSpec(prefix, suffix, color, sortPriority, playerTag)

        findTeamOfPlayer(player)?.apply {
            this.team.removePlayer(player)
            playerToSpecMappings.remove(player.uniqueId)
        }

        team.team.addPlayer(player)
        playerToSpecMappings[player.uniqueId] = team.uniqueId
    }

    private fun removeFromExisting(player: Player)
    {
        val team = findTeamOfPlayer(player)
            ?: return

        team.team.removePlayer(player)
    }

    fun removeFromAll(player: Player)
    {
        removeFromExisting(player)
    }
}

inline fun <reified O, reified A> attribute(accessor: KProperty1<O, A>): FunctionalSimpleAttribute<O, A>
{
    return FunctionalSimpleAttribute(
        O::class.java,
        A::class.java,
        accessor.javaClass.simpleName
    ) { accessor.get(it) }
}

// https://github.com/npgall/cqengine/blob/master/documentation/OtherJVMLanguages.md
inline fun <reified O, reified A> propertyAttribute(accessor: KProperty1<O, A>): FunctionalSimpleAttribute<O, A>
{
    return FunctionalSimpleAttribute(
        O::class.java,
        A::class.java,
        accessor.name
    ) { accessor.get(it) }
}

/**
 *
 * @author TehNeon
 * @since 2024-04-04
 **/
private val chatColors = ChatColor.entries
    .filter { it.asBungee().color != null }
    .associateWith {
        TextColor.color(
            it.asBungee().color.red,
            it.asBungee().color.green,
            it.asBungee().color.blue
        )
    }

fun TextColor.closestChatColor() = closestChatColor(value())
    ?: ChatColor.WHITE

fun closestChatColor(rgbValue: Int): ChatColor?
{
    var closestColor: ChatColor? = null
    var minDistance = Int.MAX_VALUE

    for ((key, value) in chatColors.entries)
    {
        val distance = rgbDistance(value.value(), rgbValue)
        if (distance < minDistance)
        {
            minDistance = distance
            closestColor = key
        }
    }
    return closestColor
}

private fun rgbDistance(color1: Int, color2: Int): Int
{
    val r1 = (color1 shr 16) and 0xFF
    val g1 = (color1 shr 8) and 0xFF
    val b1 = color1 and 0xFF

    val r2 = (color2 shr 16) and 0xFF
    val g2 = (color2 shr 8) and 0xFF
    val b2 = color2 and 0xFF

    val dr = r2 - r1
    val dg = g2 - g1
    val db = b2 - b1

    return dr * dr + dg * dg + db * db
}
