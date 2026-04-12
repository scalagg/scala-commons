

package net.evilblock.cubed.entity.npc.listener

import gg.scala.commons.annotations.Listeners
import gg.scala.flavor.inject.Inject
import net.evilblock.cubed.ScalaCommonsSpigot
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

@Listeners
object NpcEntityTeamUpdatesListeners : Listener
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent)
    {
        event.player.removeMetadata(
            "NPC_NAME_SB", plugin
        )
    }
}
