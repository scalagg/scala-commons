package gg.scala.commons.sign

import com.comphenix.packetwrapper.WrapperPlayClientUpdateSign
import com.comphenix.protocol.PacketType
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import me.lucko.helper.Events
import me.lucko.helper.protocol.Protocol
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

/**
 * @author GrowlyX
 * @since 8/22/2022
 */
@Service
object Signs
{
    private val signs = mutableMapOf<UUID, Sign>()

    @Configure
    fun configure()
    {
        Protocol
            .subscribe(PacketType.Play.Client.UPDATE_SIGN)
            .handler {
                val event = WrapperPlayClientUpdateSign(it.packet)

                val sign = signs[it.player.uniqueId]
                    ?: return@handler

                kotlin.runCatching {
                    sign.action(event.lines)
                }.onFailure { throwable ->
                    throwable.printStackTrace()
                }

                sign.close()

                signs.remove(it.player.uniqueId)
            }

        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                signs.remove(it.player.uniqueId)
            }
    }
}
