package gg.scala.commons.tablist

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerOptions
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import io.github.nosequel.tab.shared.TabHandler
import io.github.nosequel.tab.shared.entry.TabElement
import me.lucko.helper.Events
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.config.CommonsTablistConfig
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 8/8/2022
 */
@Service
object TablistService : ScheduledExecutorService by Executors
    .newSingleThreadScheduledExecutor(), PacketAdapter(
        AdapterParameteters()
            .plugin(ScalaCommonsSpigot.instance)
            .types(PacketType.Play.Server.PLAYER_INFO)
            .options(ListenerOptions.ASYNC)
            .listenerPriority(ListenerPriority.HIGHEST)
    )
{
    @Inject
    lateinit var commons: ScalaCommonsSpigot

    val cachedElements = ConcurrentHashMap<UUID, TabElement>()
    val displaying = mutableSetOf<UUID>()

    private var handler: TabHandler? = null
    var populator: TablistPopulator? = null

    var tabListTicksCalculator: (Player) -> Long? = { null }

    override fun onPacketSending(event: PacketEvent)
    {
        val wrapped = WrapperPlayServerPlayerInfo(event.packet)
        if (event.player.uniqueId in displaying)
        {
            val firstAlivePlayer = wrapped.data
                .firstOrNull { Bukkit.getPlayer(it.profile.uuid) != null }

            if (firstAlivePlayer != null)
            {
                event.isCancelled = true
            }
        }
    }

    @Configure
    fun configure()
    {
        val config = commons.config<CommonsTablistConfig>()

        if (!config.enabled)
        {
            commons.logger.info("Skipping tablist initialization!")
            return
        }

        ProtocolLibrary.getProtocolManager().addPacketListener(this)

        Events.subscribe(PlayerJoinEvent::class.java)
            .handler {
                var ticks = tabListTicksCalculator.invoke(it.player)

                if (
                    config.continueInitializationIfNullInvokation &&
                    ticks == null
                )
                {
                    ticks = config.tablistInitializeTicks.toLong()
                } else
                {
                    return@handler
                }

                Tasks.delayed(ticks) {
                    this.populator
                        ?.displayPreProcessor(it.player)
                        ?.thenAccept { display ->
                            if (display)
                            {
                                this.displaying.add(it.player.uniqueId)
                            }
                        }
                }
            }
            .bindWith(commons)

        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                this.cachedElements.remove(it.player.uniqueId)
                this.displaying.remove(it.player.uniqueId)
            }
            .bindWith(commons)

        this.scheduleAtFixedRate(
            TablistExecutor, 0L, 50L, TimeUnit.MILLISECONDS
        )
    }

    fun register(populator: TablistPopulator)
    {
        this.populator = populator

        this.handler = TabHandler({
            if (it.uniqueId !in this.displaying)
            {
                return@TabHandler null
            }

            this.cachedElements[it.uniqueId]
        }, commons, 2L)
    }
}
