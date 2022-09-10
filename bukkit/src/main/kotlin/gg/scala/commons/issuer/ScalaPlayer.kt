package gg.scala.commons.issuer

import gg.scala.commons.ExtendedScalaPlugin
import net.evilblock.cubed.util.Color
import net.evilblock.cubed.util.bukkit.FancyMessage
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

/**
 * Issuer-aware context wrapper
 * for Bukkit [Player]s.
 *
 * @author GrowlyX
 * @since 9/9/2022
 */
data class ScalaPlayer(
    private val player: Player,
    private val audiences: BukkitAudiences,
    val plugin: ExtendedScalaPlugin
)
{
    val uniqueId: UUID
        get() = this.player.uniqueId

    inline fun <reified T> plugin() = this.plugin as T

    fun bukkit() = this.player
    fun audience() = this.audiences.player(player)

    fun sendMessage(vararg messages: String)
    {
        for (message in messages)
        {
            player.sendMessage(
                Color.translate(message)
            )
        }
    }

    fun sendMessage(vararg components: Component)
    {
        val audience = this.audience()

        for (component in components)
        {
            audience.sendMessage(component)
        }
    }

    fun sendMessage(vararg messages: FancyMessage)
    {
        for (message in messages)
        {
            message.sendToPlayer(player)
        }
    }

    fun teleport(location: Location)
    {
        // TODO: Use PaperLib for asynchronous teleportation
        this.player.teleport(location)
    }
}
