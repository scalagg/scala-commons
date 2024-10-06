package gg.scala.commons.tablist

import com.comphenix.packetwrapper.MultiVersionWrapperPlayServerPlayerInfo
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
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
import net.evilblock.cubed.ScalaCommonsSpigot
import org.bukkit.entity.Player
import java.util.*

/**
 * @author GrowlyX
 * @since 2/19/2024
 */
object BuiltInV1TablistSystemProvider : TablistSystemProvider, PacketAdapter(
    AdapterParameteters()
        .plugin(ScalaCommonsSpigot.instance)
        .types(PacketType.Play.Server.PLAYER_INFO)
        .listenerPriority(ListenerPriority.HIGHEST)
)
{

    override fun onPacketSending(event: PacketEvent)
    {

        if (Util.getClientVersion(event.player) > 5)
        {
            return
        }

        val wrapped = MultiVersionWrapperPlayServerPlayerInfo(event.packet)
        val data = wrapped.data[0]

        if (wrapped.action == EnumWrappers.PlayerInfoAction.ADD_PLAYER && data.latency != -1)
        {
            wrapped.data = Collections.singletonList(
                PlayerInfoData(
                    data.profileId, data.latency, false, data.gameMode, data.profile, data.displayName
                )
            )
            event.isCancelled = true
            return
        }

        wrapped.data = Collections.singletonList(
            PlayerInfoData(
                data.profileId, 0, data.isListed, data.gameMode, data.profile, data.displayName
            )
        )
    }

    override fun configure()
    {
        ProtocolLibrary.getProtocolManager().addPacketListener(this)
        kotlin.runCatching {
            TablistManager.init()
        }
    }

    override fun initialize(player: Player)
    {
        val handle = TablistManager.get().getTablist(player)
        handle.cellStacking = CellStacking.VERTICAL
        handle.init()
    }

    override fun create(player: Player, display: Boolean)
    {
        val handle = TablistManager.get().getTablist(player)

        if (handle.tablistPopulator == null)
        {
            handle.tablistPopulator =
                PlayerTablistPopulator(handle, DefaultPlayersProvider.getInstance())
        }

        if (display)
        {
            handle.isEnabled = true
        }
    }

    override fun destroy(player: Player)
    {
        TablistManager.get().release(player)
    }
}
