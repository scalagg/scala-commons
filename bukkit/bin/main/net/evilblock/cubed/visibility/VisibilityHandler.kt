package net.evilblock.cubed.visibility

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.player.GameMode
import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.protocol.player.UserProfile
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate
import net.evilblock.cubed.util.bukkit.Tasks
import gg.scala.commons.tablist.util.Util
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode as BukkitGameMode
import org.bukkit.entity.Player
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import java.util.LinkedHashMap

object VisibilityHandler {

    private val adapters = LinkedHashMap<String, VisibilityAdapter>()
    private val overrideHandlers = LinkedHashMap<String, OverrideHandler>()
    private var playerListPolicy: PlayerListVisibilityPolicy? = null
    private var selectivePolicyProvider: ((Player) -> PlayerListVisibilityPolicy)? = null

    fun registerAdapter(identifier: String, adapter: VisibilityAdapter) {
        adapters[identifier] = adapter
    }

    fun registerOverride(identifier: String, handler: OverrideHandler) {
        overrideHandlers[identifier] = handler
    }

    fun setPlayerListVisibilityPolicy(policy: PlayerListVisibilityPolicy) {
        playerListPolicy = policy
    }

    fun selectivelySetPolicy(provider: (Player) -> PlayerListVisibilityPolicy) {
        selectivePolicyProvider = provider
    }

    fun getPolicyFor(player: Player): PlayerListVisibilityPolicy {
        return selectivePolicyProvider?.invoke(player) ?: playerListPolicy ?: PlayerListVisibilityPolicy.Global
    }

    fun update(player: Player) {
        if (!Bukkit.isPrimaryThread()) {
            Tasks.sync {
                updateAllTo(player)
                updateToAll(player)
            }
        } else {
            updateAllTo(player)
            updateToAll(player)
        }
    }

    fun updateAllTo(viewer: Player) {
        for (target in Bukkit.getOnlinePlayers()) {
            if (!shouldSee(target, viewer)) {
                viewer.hidePlayer(target); removePlayer(target, viewer)
            } else {
                viewer.showPlayer(target); addPlayer(target, viewer)
            }
        }
    }

    fun updateToAll(target: Player) {
        for (viewer in Bukkit.getOnlinePlayers()) {
            if (!shouldSee(target, viewer)) {
                viewer.hidePlayer(target); removePlayer(target, viewer)
            } else {
                viewer.showPlayer(target); addPlayer(target, viewer)
            }
        }
    }

    fun removePlayer(player: Player, other: Player)
    {
        if (!player.hasMetadata("vanished")) {
            return
        }

        Tasks.delayed(1L) {
            if (net.evilblock.cubed.util.ServerVersion.getVersion().isOlderThan(net.evilblock.cubed.util.ServerVersion.v1_19)) {
                PacketEvents.getAPI().playerManager.sendPacket(
                    other,
                    WrapperPlayServerPlayerInfo(
                        WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER,
                        WrapperPlayServerPlayerInfo.PlayerData(null, UserProfile(player.uniqueId, player.name), null, -1)
                    )
                )
            } else {
                PacketEvents.getAPI().playerManager.sendPacket(other, WrapperPlayServerPlayerInfoRemove(player.uniqueId))
            }
        }
    }

    private fun Player.constructLegacyData(): WrapperPlayServerPlayerInfo.PlayerData
    {
        val profile = Util.getPlayerProfile(this)
        val userProfile = UserProfile(profile.id, profile.name).apply {
            val skin = profile.skinData
            if (skin != null) {
                textureProperties.add(TextureProperty("textures", skin.value, skin.signature))
            }
        }

        val component = GsonComponentSerializer.gson().deserialize(
            GsonComponentSerializer.gson().serialize(
                LegacyComponentSerializer.legacySection().deserialize(displayName)
            )
        )

        return WrapperPlayServerPlayerInfo.PlayerData(
            component,
            userProfile,
            GameMode.valueOf(BukkitGameMode.SURVIVAL.name),
            ping
        )
    }

    private fun Player.constructModernData(): WrapperPlayServerPlayerInfoUpdate.PlayerInfo
    {
        val profile = Util.getPlayerProfile(this)
        val userProfile = UserProfile(profile.id, profile.name).apply {
            val skin = profile.skinData
            if (skin != null) {
                textureProperties.add(TextureProperty("textures", skin.value, skin.signature))
            }
        }

        val component = GsonComponentSerializer.gson().deserialize(
            GsonComponentSerializer.gson().serialize(
                LegacyComponentSerializer.legacySection().deserialize(displayName)
            )
        )

        return WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
            userProfile,
            true,
            ping,
            GameMode.valueOf(BukkitGameMode.SURVIVAL.name),
            component,
            null
        )
    }

    fun addPlayer(player: Player, other: Player)
    {
        if (!player.hasMetadata("vanished")) {
            return
        }

        Tasks.delayed(1L) {
            if (net.evilblock.cubed.util.ServerVersion.getVersion().isOlderThan(net.evilblock.cubed.util.ServerVersion.v1_19)) {
                PacketEvents.getAPI().playerManager.sendPacket(
                    other,
                    WrapperPlayServerPlayerInfo(
                        WrapperPlayServerPlayerInfo.Action.ADD_PLAYER,
                        player.constructLegacyData()
                    )
                )
            } else {
                PacketEvents.getAPI().playerManager.sendPacket(
                    other,
                    WrapperPlayServerPlayerInfoUpdate(
                        WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER,
                        player.constructModernData()
                    )
                )
            }
        }
    }

    fun treatAsOnline(target: Player, viewer: Player): Boolean {
        return !target.hasMetadata("vanished")
    }

    private fun shouldSee(target: Player, viewer: Player): Boolean {
        for (handler in overrideHandlers.values) {
            if (handler.getAction(target, viewer) === OverrideAction.SHOW) {
                return true
            }
        }

        for (handler in adapters.values) {
            if (handler.getAction(target, viewer) === VisibilityAction.HIDE) {
                return false
            }
        }

        return true
    }

    fun getDebugInfo(target: Player, viewer: Player): List<String> {
        val debug = ArrayList<String>()
        var canSee: Boolean? = null

        for ((key, handler) in overrideHandlers) {
            val action = handler.getAction(target, viewer)
            var color = ChatColor.GRAY

            if (action === OverrideAction.SHOW && canSee == null) {
                canSee = true
                color = ChatColor.GREEN
            }

            debug.add(color.toString() + "Overriding Handler: $key: $action")
        }

        for ((key, handler) in adapters) {
            val action = handler.getAction(target, viewer)
            var color = ChatColor.GRAY

            if (action === VisibilityAction.HIDE && canSee == null) {
                canSee = false
                color = ChatColor.GREEN
            }

            debug.add(color.toString() + "Normal Handler: $key: $action")
        }

        if (canSee == null) {
            canSee = true
        }

        debug.add(ChatColor.AQUA.toString() + "Result: " + viewer.name + " " + (if (canSee) "can" else "cannot") + " see " + target.name)

        return debug
    }
}
