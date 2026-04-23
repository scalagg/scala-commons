package gg.scala.commons.event

import gg.scala.commons.sentry.SentryService
import net.evilblock.cubed.ScalaCommonsSpigot
import org.bukkit.event.Cancellable
import java.util.logging.Level

/**
 * @author GrowlyX
 * @since 7/30/2022
 */
abstract class StatefulEvent : AbstractEvent<EventQuadState>(), Cancellable
{
    private var state = EventQuadState.NOT_SET

    /**
     * The state is automatically updated through Bukkit's Cancellable logic.
     */
    override fun postPushCompute() = this.state

    override fun postPushFailure(throwable: Throwable): EventQuadState
    {
        SentryService.push(throwable)

        ScalaCommonsSpigot.instance.logger.log(
            Level.SEVERE, "Exception thrown during stateful event push", throwable
        )

        return EventQuadState.FAILED
    }

    override fun isCancelled() =
        state == EventQuadState.CANCELLED

    override fun setCancelled(cancel: Boolean)
    {
        when (cancel)
        {
            true -> this.state = EventQuadState.CANCELLED
            false -> this.state = EventQuadState.ALLOWED
        }
    }
}
