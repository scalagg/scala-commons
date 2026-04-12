package gg.scala.commons.playerstatus

import mc.arch.player.status.VisibilityGroup
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import java.util.UUID

/**
 * @author Limo
 * @date 2/5/2026
 */
enum class PlayerStatusType(val displayName: String)
{
    ONLINE("Online"),
    PLAYING("Playing"),
    IN_QUEUE("In Queue"),
    SPECTATING("Spectating"),
    IN_PARTY("In Party"),
    AFK("AFK"),
    OFFLINE("Offline");

    fun isPlaying() = this == PLAYING
    fun isSpectating() = this == SPECTATING
    fun isInQueue() = this == IN_QUEUE
}

/**
 * @author Limo
 * @date 2/5/2026
 */
class PlayerStatus(
    var type: PlayerStatusType = PlayerStatusType.ONLINE,
    var activityDescription: String = "",
    var returnToServerGroup: String = "hub",
    var visibilityGroup: VisibilityGroup = VisibilityGroup.ALL
)
{
    fun isPlaying() = type.isPlaying()
    fun isSpectating() = type.isSpectating()
    fun isInQueue() = type.isInQueue()
    fun visibilityGroup() = visibilityGroup
}

/**
 * @author Limo
 * @date 2/5/2026
 */
class PlayerStatusPreUpdateEvent(
    val player: Player,
    val playerStatus: PlayerStatus = PlayerStatus(),
    val previousStatus: PlayerStatus = PlayerStatus()
) : Event(false), Cancellable
{
    private var cancelled = false

    companion object
    {
        @JvmStatic
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList() = handlerList
    }

    override fun getHandlers() = handlerList
    override fun isCancelled() = cancelled
    override fun setCancelled(cancel: Boolean) { cancelled = cancel }
}

fun Player.toPlayerStatus(): PlayerStatus
{
    val type = when
    {
        hasMetadata("spectating") -> PlayerStatusType.SPECTATING
        hasMetadata("playing") -> PlayerStatusType.PLAYING
        hasMetadata("queue") -> PlayerStatusType.IN_QUEUE
        hasMetadata("party") -> PlayerStatusType.IN_PARTY
        else -> PlayerStatusType.ONLINE
    }
    return PlayerStatus(type)
}

fun UUID.toPlayerStatus(): PlayerStatus
{
    val player = org.bukkit.Bukkit.getPlayer(this) ?: return PlayerStatus(PlayerStatusType.OFFLINE)
    return player.toPlayerStatus()
}
