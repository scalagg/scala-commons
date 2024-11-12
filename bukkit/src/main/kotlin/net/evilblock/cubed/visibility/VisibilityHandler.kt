package net.evilblock.cubed.visibility

import com.comphenix.packetwrapper.MultiVersionWrapperPlayServerPlayerInfo
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedGameProfile
import com.comphenix.protocol.wrappers.WrappedSignedProperty
import com.mojang.authlib.GameProfile
import me.lucko.helper.protocol.Protocol
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.nms.MinecraftReflection
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player

object VisibilityHandler
{

    private val adapters = LinkedHashMap<String, VisibilityAdapter>()
    private val overrideHandlers = LinkedHashMap<String, OverrideHandler>()

    fun registerAdapter(identifier: String, adapter: VisibilityAdapter)
    {
        adapters[identifier] = adapter
    }

    fun registerOverride(identifier: String, handler: OverrideHandler)
    {
        overrideHandlers[identifier] = handler
    }

    fun update(player: Player)
    {
        if (!Bukkit.isPrimaryThread())
        {
            Tasks.sync {
                updateAllTo(player)
                updateToAll(player)
            }
        } else
        {
            updateAllTo(player)
            updateToAll(player)
        }
    }

    fun updateAllTo(viewer: Player)
    {
        for (target in Bukkit.getOnlinePlayers())
        {
            if (!shouldSee(target, viewer))
            {
                viewer.hidePlayer(target); removePlayer(target, viewer)
            } else
            {
                viewer.showPlayer(target); addPlayer(target, viewer)
            }
        }
    }

    fun updateToAll(target: Player)
    {
        for (viewer in Bukkit.getOnlinePlayers())
        {
            if (!shouldSee(target, viewer))
            {
                viewer.hidePlayer(target); removePlayer(target, viewer)
            } else
            {
                viewer.showPlayer(target); addPlayer(target, viewer)
            }
        }
    }

    fun removePlayer(player: Player, other: Player)
    {
        if (player.hasMetadata("vanished"))
        {
            val playerInfoAdd = MultiVersionWrapperPlayServerPlayerInfo()
            playerInfoAdd.action = EnumWrappers.PlayerInfoAction.REMOVE_PLAYER
            playerInfoAdd.data = listOf(player.constructData())

            Tasks.delayed(1L)
            {
                Protocol.sendPacket(other, playerInfoAdd.handle)
            }
        }
    }

    fun Player.constructData(): PlayerInfoData
    {
        val profile = WrappedGameProfile(uniqueId, name)
        val gameProfile = MinecraftReflection.getGameProfile(player) as GameProfile

        val textures = gameProfile.properties.get("textures").first()
        profile.properties.put("textures", WrappedSignedProperty("textures", textures.value, textures.signature))

        val gameMode = NativeGameMode.fromBukkit(GameMode.SURVIVAL)
        val component = WrappedChatComponent.fromJson(
            GsonComponentSerializer.gson().serialize(
                LegacyComponentSerializer.legacySection().deserialize(displayName)
            )
        )

        return PlayerInfoData(uniqueId, ping, true, gameMode, profile, component)
    }

    fun addPlayer(player: Player, other: Player)
    {
        if (player.hasMetadata("vanished"))
        {
            val playerInfoAdd = MultiVersionWrapperPlayServerPlayerInfo()
            playerInfoAdd.action = EnumWrappers.PlayerInfoAction.ADD_PLAYER
            playerInfoAdd.data = listOf(player.constructData())

            Tasks.delayed(1L)
            {
                Protocol.sendPacket(other, playerInfoAdd.handle)
            }
        }
    }

    fun treatAsOnline(target: Player, viewer: Player): Boolean
    {
        return !target.hasMetadata("vanished")
    }

    private fun shouldSee(target: Player, viewer: Player): Boolean
    {
        for (handler in overrideHandlers.values)
        {
            if (handler.getAction(target, viewer) === OverrideAction.SHOW)
            {
                return true
            }
        }

        for (handler in adapters.values)
        {
            if (handler.getAction(target, viewer) === VisibilityAction.HIDE)
            {
                return false
            }
        }

        return true
    }

    fun getDebugInfo(target: Player, viewer: Player): List<String>
    {
        val debug = ArrayList<String>()
        var canSee: Boolean? = null

        for ((key, handler) in overrideHandlers)
        {
            val action = handler.getAction(target, viewer)
            var color = ChatColor.GRAY

            if (action === OverrideAction.SHOW && canSee == null)
            {
                canSee = true
                color = ChatColor.GREEN
            }

            debug.add(color.toString() + "Overriding Handler: $key: $action")
        }

        for ((key, handler) in adapters)
        {
            val action = handler.getAction(target, viewer)
            var color = ChatColor.GRAY

            if (action === VisibilityAction.HIDE && canSee == null)
            {
                canSee = false
                color = ChatColor.GREEN
            }

            debug.add(color.toString() + "Normal Handler: $key: $action")
        }

        if (canSee == null)
        {
            canSee = true
        }

        debug.add(ChatColor.AQUA.toString() + "Result: " + viewer.name + " " + (if (canSee) "can" else "cannot") + " see " + target.name)

        return debug
    }

}
