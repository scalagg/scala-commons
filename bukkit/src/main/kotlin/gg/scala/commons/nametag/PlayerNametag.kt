package gg.scala.commons.nametag

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import me.lucko.helper.Events
import me.lucko.helper.terminable.Terminable
import net.evilblock.cubed.ScalaCommonsSpigot
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*

/**
 * @author GrowlyX
 * @since 3/30/2024
 */
fun Player.nametag() = PlayerNametagHandler.nametagOf(this)

@Service
@IgnoreAutoScan
object PlayerNametagHandler
{
    private val playerNametags = mutableMapOf<UUID, PlayerNametag>()

    @Configure
    fun configure()
    {
        Events.subscribe(PlayerJoinEvent::class.java)
            .handler {
                playerNametags[it.player.uniqueId] = PlayerNametag(it.player)
                    .apply {
                        bindWith(ScalaCommonsSpigot.instance)
                    }
            }
            .bindWith(ScalaCommonsSpigot.instance)
    }

    fun nametagOf(player: Player) = playerNametags[player.uniqueId]
        ?: throw IllegalStateException(
            "Player does not have a nametag object"
        )
}

class PlayerNametag(private val player: Player) : Terminable
{
    fun update(
        prefix: String, suffix: String, color: ChatColor? = null,
        priority: Int = 0, playerTag: Boolean = false
    )
    {
        /*NametagService.setNametagOfPlayer(
            player,
            LegacyComponentSerializer
                .legacySection()
                .deserialize(prefix),
            LegacyComponentSerializer
                .legacySection()
                .deserialize(suffix),
            color = color,
            priority, playerTag
        )*/
    }

    override fun close()
    {
        NametagService.removeFromAll(player)
    }
}
