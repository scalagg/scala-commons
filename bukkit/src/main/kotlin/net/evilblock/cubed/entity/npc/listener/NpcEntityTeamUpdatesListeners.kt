/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.npc.listener

import gg.scala.commons.annotations.Listeners
import net.evilblock.cubed.Cubed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

@Listeners
object NpcEntityTeamUpdatesListeners : Listener {

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        event.player.removeMetadata("NPC_NAME_SB", Cubed.instance)
    }

}
