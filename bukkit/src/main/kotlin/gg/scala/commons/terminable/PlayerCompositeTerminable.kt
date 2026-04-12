package gg.scala.commons.terminable

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import me.lucko.helper.Events
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.ScalaCommonsSpigot
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

@Service
object PlayerCompositeTerminable
{
    val terminables =
        mutableMapOf<UUID, CompositeTerminable>()

    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    @Configure
    fun configure()
    {
        Events.subscribe(PlayerJoinEvent::class.java)
            .handler {
                this.terminables[it.player.uniqueId] =
                    CompositeTerminable.create()
            }
            .bindWith(plugin)

        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                this.terminables
                    .remove(it.player.uniqueId)
                    ?.closeAndReportException()
            }
            .bindWith(plugin)
    }

    fun of(player: Player): CompositeTerminable
    {
        return this.terminables[player.uniqueId] ?: CompositeTerminable
            .create().apply { terminables[player.uniqueId] = this }
    }
}
