package gg.scala.commons.tablist

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo
import gg.scala.commons.tablist.adapter.populator.DefaultPlayersProvider
import gg.scala.commons.tablist.adapter.populator.PlayerTablistPopulator
import gg.scala.commons.tablist.cell.CellStacking
import gg.scala.commons.tablist.manage.TablistManager
import gg.scala.commons.tablist.util.Util
import net.evilblock.cubed.ScalaCommonsSpigot
import org.bukkit.entity.Player
import java.util.Collections

object BuiltInV1TablistSystemProvider : TablistSystemProvider, PacketListenerAbstract(PacketListenerPriority.HIGHEST)
{
    private var registered = false

    override fun onPacketSend(event: PacketSendEvent)
    {
        if (event.packetType != PacketType.Play.Server.PLAYER_INFO) {
            return
        }

        val player = event.getPlayer<Player>() ?: return
        if (Util.getClientVersion(player) > 5) {
            return
        }

        val wrapped = WrapperPlayServerPlayerInfo(event)
        val data = wrapped.playerDataList.firstOrNull() ?: return

        if (wrapped.action == WrapperPlayServerPlayerInfo.Action.ADD_PLAYER && data.ping != -1)
        {
            wrapped.playerDataList = Collections.singletonList(
                WrapperPlayServerPlayerInfo.PlayerData(
                    data.displayName,
                    data.userProfile,
                    data.gameMode,
                    -1
                )
            )
            event.isCancelled = true
            return
        }

        wrapped.playerDataList = Collections.singletonList(
            WrapperPlayServerPlayerInfo.PlayerData(
                data.displayName,
                data.userProfile,
                data.gameMode,
                0
            )
        )
    }

    override fun configure()
    {
        if (!registered) {
            PacketEvents.getAPI().eventManager.registerListener(this)
            registered = true
        }
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
            handle.tablistPopulator = PlayerTablistPopulator(handle, DefaultPlayersProvider.getInstance())
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
