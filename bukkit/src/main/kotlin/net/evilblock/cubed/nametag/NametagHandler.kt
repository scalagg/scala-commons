package net.evilblock.cubed.nametag

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.config.CommonsNametagConfig
import net.evilblock.cubed.nametag.update.NametagUpdate
import net.evilblock.cubed.nametag.update.NametagUpdateEvent
import net.evilblock.cubed.scoreboard.MultiVersionScoreboardTeamPackets
import net.evilblock.cubed.scoreboard.send
import net.evilblock.cubed.services.CommonsServiceExecutor
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Service
object NametagHandler
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    internal val teamMap = ConcurrentHashMap<UUID, HashMap<UUID, NametagInfo>>()

    private val registeredTeams = Collections.synchronizedList(ArrayList<NametagInfo>())
    private var teamCreateIndex = 0

    private var providers = CopyOnWriteArrayList<NametagProvider>()
    val providerLock = ReentrantLock()

    private var isNametagRestrictionEnabled: Boolean = false
    private var nametagRestrictBypass: String = ""

    var async: Boolean = false
    var updateInterval: Int = 2

    @Configure
    fun configure()
    {
        val nametagConfig = this.plugin.config<CommonsNametagConfig>()
        if (nametagConfig.enabled)
        {
            plugin.logger.info("Auto-updating nametags are disabled by config")
            return
        }

        async = nametagConfig.pushAsyncUpdates
        updateInterval = nametagConfig.asyncUpdatesIntervalTicks

        isNametagRestrictionEnabled = nametagConfig.packetRestriction
        nametagRestrictBypass = nametagConfig.packetRestrictionPrefix

        if (async)
        {
            CommonsServiceExecutor
                .scheduleAtFixedRate(
                    NametagThread(),
                    0L,
                    updateInterval * 50L,
                    TimeUnit.MILLISECONDS
                )
        }

        /*if (serverNewerThan18)
        {
            plugin.flavor {
                inject(NametagService)
                inject(PlayerNametagHandler)
            }
        }*/

        registerProvider(NametagProvider.DefaultNametagProvider())
    }

    fun registerProvider(newProvider: NametagProvider)
    {
        providerLock.withLock {
            providers.add(newProvider)
            providers.sortByDescending { it.weight }
        }
    }

    fun reloadPlayer(toRefresh: Player)
    {
        val update = NametagUpdate(toRefresh)

        if (async)
        {
            NametagThread.pendingUpdates[update] = true
        } else
        {
            Tasks.sync {
                applyUpdate(update)
            }
        }
    }

    fun reloadOthersFor(refreshFor: Player)
    {
        for (toRefresh in Bukkit.getOnlinePlayers())
        {
            if (refreshFor === toRefresh)
            {
                continue
            }
            reloadPlayer(toRefresh, refreshFor)
        }
    }

    fun reloadPlayer(toRefresh: Player, refreshFor: Player)
    {
        val update = NametagUpdate(toRefresh, refreshFor)

        if (async)
        {
            NametagThread.pendingUpdates[update] = true
        } else
        {
            applyUpdate(update)
        }
    }

    internal fun applyUpdate(nametagUpdate: NametagUpdate)
    {
        val toRefreshPlayer = Bukkit.getPlayer(nametagUpdate.toRefresh) ?: return
        if (nametagUpdate.refreshFor == null)
        {
            for (refreshFor in Bukkit.getOnlinePlayers())
            {
                reloadPlayerInternal(toRefreshPlayer, refreshFor)
            }
        } else
        {
            val refreshForPlayer = Bukkit.getPlayer(nametagUpdate.refreshFor)
            if (refreshForPlayer != null)
            {
                reloadPlayerInternal(toRefreshPlayer, refreshForPlayer)
            }
        }
    }

    private fun reloadPlayerInternal(toRefresh: Player, refreshFor: Player)
    {
        if (!refreshFor.hasMetadata("Nametag-Applied"))
        {
            return
        }

        if (this.providers.isEmpty)
        {
            return
        }

        var provided: NametagInfo? = null
        var providerIndex = 0

        NametagUpdateEvent(toRefresh).call()

        while (provided == null && providerIndex < providers.size)
        {
            provided = providers
                .getOrNull(providerIndex++)
                ?.fetchNametag(toRefresh, refreshFor)
        }

        if (provided == null)
        {
            return
        }

        if (isNametagRestrictionEnabled)
        {
            val prefix = provided.prefix
            if (!prefix.equals(nametagRestrictBypass, ignoreCase = true))
            {
                return
            }
        }

        var teamInfoMap = HashMap<UUID, NametagInfo>()
        if (teamMap.containsKey(refreshFor.uniqueId))
        {
            teamInfoMap = teamMap[refreshFor.uniqueId]!!
        }

        MultiVersionScoreboardTeamPackets(provided.name)
            .newTeamMemberUpdatePacket(
                toRefresh.name,
                WrapperPlayServerTeams.TeamMode.ADD_ENTITIES
            )
            .send(refreshFor)

        teamInfoMap[toRefresh.uniqueId] = provided
        teamMap[refreshFor.uniqueId] = teamInfoMap
    }

    internal fun initiatePlayer(player: Player)
    {
        for (teamInfo in registeredTeams)
        {
            (teamInfo.teamAddPacket as PacketWrapper<*>).send(player)
        }
    }

    internal fun getOrCreate(
        prefix: String, suffix: String,
        name: String = teamCreateIndex++.toString()
    ): NametagInfo
    {
        for (teamInfo in registeredTeams)
        {
            if (teamInfo.prefix == prefix && teamInfo.suffix == suffix)
            {
                return teamInfo
            }
        }

        val newTeam = NametagInfo(name, prefix, suffix)
        registeredTeams.add(newTeam)

        val addPacket = newTeam.teamAddPacket
        for (player in Bukkit.getOnlinePlayers())
        {
            (addPacket as PacketWrapper<*>).send(player)
        }

        return newTeam
    }
}
