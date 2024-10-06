package gg.scala.commons.modern

import com.destroystokyo.paper.profile.PlayerProfile
import com.mojang.authlib.GameProfile
import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import net.evilblock.cubed.serializers.Serializers
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_20_R3.profile.CraftPlayerTextures
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.java.JavaPlugin
import java.net.URL
import java.util.UUID
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

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

    data class Skin(
        val url: String
    )

    data class Textures(
        val SKIN: Skin
    )

    data class TextureResponse(
        val textures: Textures
    )

    @OptIn(ExperimentalEncodingApi::class)
    fun applySkullTexture(stack: ItemStack, texture: String): ItemStack
    {
        val skullMeta = (stack.itemMeta as SkullMeta)
        val profile = Bukkit.createPlayerProfile(UUID.randomUUID())
        val newTextures = profile.textures
        newTextures.skin = URL(
            Serializers.gson
                .fromJson<TextureResponse>(String(Base64.decode(texture)), TextureResponse::class.java)
                .textures.SKIN.url
        )

        profile.setTextures(newTextures)
        skullMeta.playerProfile = profile as PlayerProfile
        stack.itemMeta = skullMeta
        return stack
    }
}
