package gg.scala.commons.playerstatus

import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * @author Limo
 * @date 2/5/2026
 */
object PlayerStatusTrackerService
{
    data class PlayerStatus(
        val uniqueId: UUID,
        val username: String,
        val prefixedName: String,
        val isOnline: Boolean = true,
        val activityDescription: String = "Unknown"
    )

    fun loadStatusOf(uniqueId: UUID): CompletableFuture<PlayerStatus?>
    {
        return CompletableFuture.completedFuture(
            PlayerStatus(
                uniqueId = uniqueId,
                username = "Unknown",
                prefixedName = "Unknown",
                activityDescription = "In Lobby"
            )
        )
    }
}

fun Player.isVirtuallyInvisibleToSomeExtent(): Boolean = false

fun Player.canVirtuallySee(other: Player): Boolean = true

fun Player.canVirtuallySee(other: UUID): Boolean = true
