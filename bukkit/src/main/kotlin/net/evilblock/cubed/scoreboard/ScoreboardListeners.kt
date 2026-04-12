

package net.evilblock.cubed.scoreboard

import gg.scala.commons.annotations.Listeners
import gg.scala.flavor.inject.Inject
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerJoinEvent

@Listeners
object ScoreboardListeners : Listener
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerJoinEvent(event: PlayerJoinEvent)
    {
        ScoreboardHandler
            .scoreboardProvider ?: return

        Tasks.delayed(1L) {
            ScoreboardHandler.scoreboards[event.player.uniqueId] =
                Scoreboard(event.player, plugin)
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent)
    {
        ScoreboardHandler.scoreboards.remove(event.player.uniqueId)
    }
}
