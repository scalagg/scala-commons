package net.evilblock.cubed.reboot.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Event fired when the server is shutting down or rebooting.
 * Used to perform cleanup and notify systems of impending shutdown.
 *
 * @author Limo
 * @date 2/3/26
 */
class ServerShutdownEvent(
    val reason: ShutdownReason = ShutdownReason.UNKNOWN,
    val message: String = "Server is shutting down"
) : Event(false)
{
    companion object
    {
        @JvmStatic
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList() = handlerList
    }

    override fun getHandlers() = handlerList
}

enum class ShutdownReason
{
    REBOOT,
    MAINTENANCE,
    CRASH,
    MANUAL,
    UNKNOWN
}
