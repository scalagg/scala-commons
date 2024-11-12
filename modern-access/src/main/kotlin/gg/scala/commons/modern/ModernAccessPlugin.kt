package gg.scala.commons.modern

import com.mojang.authlib.GameProfile
import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author GrowlyX
 * @since 10/6/2024
 */
class ModernAccessPlugin : JavaPlugin()
{
    companion object
    {
        @JvmStatic
        internal lateinit var access: ModernAccessPlugin

        @JvmStatic
        fun instance() = access
    }

    fun sendPlayerInfoAddPacket(player: Player, sendTo: Player)
    {
        val playerInfo = ClientboundPlayerInfoUpdatePacket(
            ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
            (player as CraftPlayer).handle
        )

        (sendTo as CraftPlayer).handle.connection.send(playerInfo)
    }

    fun sendPlayerInfoRemovePacket(player: Player, sendTo: Player)
    {
        val playerInfo = ClientboundPlayerInfoRemovePacket(
            listOf(player.uniqueId)
        )

        (sendTo as CraftPlayer).handle.connection.send(playerInfo)
    }

    override fun onEnable()
    {
        access = this

        CloudSyncDiscoveryService
            .discoverable.assets
            .apply {
                add("gg.scala.commons:modern-access:modern-access-3.5.3-dev")
            }

        logger.info("Hooked into 1.20")
    }
}
