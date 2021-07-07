/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.poster

import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import java.util.*

class PosterRenderer(private val poster: Poster) : MapRenderer() {

    internal val renderedFor = hashSetOf<UUID>()
    private var drawn: Boolean = false

    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        if (renderedFor.contains(player.uniqueId)) {
            return
        }

        val imageBuffer = poster.getBuffer()
        if (imageBuffer != null) {
            if (!drawn) {
                drawn = true
                canvas.drawImage(0, 0, imageBuffer)
            }

            renderedFor.add(player.uniqueId)

            Tasks.asyncDelayed(2L) {
                player.sendMap(map)
            }
        }
    }

}
