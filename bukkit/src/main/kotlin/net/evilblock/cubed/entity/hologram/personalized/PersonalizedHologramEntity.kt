/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.hologram.personalized

import net.evilblock.cubed.entity.hologram.HologramEntity
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

abstract class PersonalizedHologramEntity(location: Location) : HologramEntity("a", location) {

    private var lastUpdate: Long = System.currentTimeMillis()

    private val sent: MutableMap<UUID, HologramEntity> = mutableMapOf()

    override fun getTypeName(): String {
        return "Personalized Hologram"
    }

    abstract fun getNewLines(player: Player): List<String>

    abstract fun getUpdateInterval(): Long

    fun canUpdate(): Boolean {
        return System.currentTimeMillis() - lastUpdate >= getUpdateInterval()
    }

    fun updateLinesFor(player: Player, lines: List<String>) {
        sent[player.uniqueId]?.updateLines(lines)
    }

    fun updateLinesForAll() {
        for (viewer in getCurrentWatcherPlayers()) {
            val newLines = getNewLines(viewer)
            updateLinesFor(viewer, newLines)
        }
    }

    override fun updateLocation(location: Location) {
        super.updateLocation(location)

        for (hologram in sent.values) {
            hologram.updateLocation(location.clone())
        }
    }

    override fun spawn(player: Player) {
        super.spawn(player)

        if (sent.containsKey(player.uniqueId)) {
            sent.remove(player.uniqueId)!!.destroy(player)
        } else {
            sent[player.uniqueId] = HologramEntity("test", location).also {
                it.initializeData()
                it.updateLines(getNewLines(player))
                it.spawn(player)
            }
        }
    }

    override fun destroy(player: Player) {
        super.destroy(player)

        sent.remove(player.uniqueId)?.destroy(player)
    }

    override fun update(player: Player) {
        super.update(player)

        sent.remove(player.uniqueId)?.update(player)
    }

    override fun sendSpawnPackets(player: Player) {

    }

    override fun sendUpdatePackets(player: Player) {

    }

    override fun sendRepositionPackets(player: Player) {

    }

    override fun sendDestroyPackets(player: Player) {

    }

}
