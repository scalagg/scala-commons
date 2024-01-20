package gg.scala.commons.tablist

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerOptions
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.PlayerInfoData
import gg.scala.commons.tablist.adapter.populator.DefaultPlayersProvider
import gg.scala.commons.tablist.adapter.populator.PlayerTablistPopulator
import gg.scala.commons.tablist.cell.CellStacking
import gg.scala.commons.tablist.manage.TablistManager
import gg.scala.commons.tablist.util.Util
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
//        val wrapped = WrapperPlayServerPlayerInfo(event.packet)
//        if (event.player.uniqueId in displaying)
//        {
//            val firstAlivePlayer = wrapped.data
//                .firstOrNull { Bukkit.getPlayer(it.profile.uuid) != null }
//
//            if (firstAlivePlayer != null)
//            {
//                event.isCancelled = true
//            }
//        }

        if (Util.getClientVersion(event.player) > 5) {
            return
        }

        val wrapped = WrapperPlayServerPlayerInfo(event.packet)
        val data = wrapped.data[0]

        if (wrapped.action == EnumWrappers.PlayerInfoAction.ADD_PLAYER && data.latency != -1) {
            event.isCancelled = true
            return
        }

        wrapped.data = Collections.singletonList(PlayerInfoData(
            data.profileId, 0, data.isListed, data.gameMode, data.profile, data.displayName))
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

        TablistManager.init()
//        TablistUpdateTask.getInstance().start()

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
                    val handle0 = TablistManager.get().getTablist(it.player)
                    handle0.cellStacking = CellStacking.VERTICAL

                    this.populator
                        ?.displayPreProcessor(it.player)
                        ?.thenAccept { display ->
                            val handle = TablistManager.get().getTablist(it.player)

                            if (handle.tablistPopulator == null) {
                                handle.tablistPopulator = PlayerTablistPopulator(handle, DefaultPlayersProvider.getInstance())
                            }

                            if (display)
                            {
                                TablistManager.get().getTablist(it.player).isEnabled = true
//                                this.displaying.add(it.player.uniqueId)
                            }
                        }
                }
            }
            .bindWith(commons)

        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
//                this.cachedElements.remove(it.player.uniqueId)
//                this.displaying.remove(it.player.uniqueId)
                TablistManager.get().release(it.player)
            }
            .bindWith(commons)

        this.scheduleAtFixedRate(
            TablistExecutor, 0L, 50L, TimeUnit.MILLISECONDS
        )
    }

    fun register(populator: TablistPopulator)
    {
        this.populator = populator

//        this.handler = TabHandler({
//            if (it.uniqueId !in this.displaying)
//            {
//                return@TabHandler null
//            }
//
//            this.cachedElements[it.uniqueId]
//        }, commons, 2L)
    }
}
