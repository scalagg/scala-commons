package gg.scala.commons.sign

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import me.lucko.helper.Events
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

@Service
object Signs
{
    private val signs = mutableMapOf<UUID, Sign>()

    @Configure
    fun configure()
    {
        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                signs.remove(it.player.uniqueId)
            }
    }

    fun track(sign: Sign)
    {
        signs[sign.uniqueId] = sign
    }

    fun remove(uniqueId: UUID)
    {
        signs.remove(uniqueId)
    }
}
