package gg.scala.commons.infrastructure

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * @author Limo
 * @date 2/5/2026
 */
class RollingUpdateStartEvent : Event(true)
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
