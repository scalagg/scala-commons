/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.npc.strategy

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.util.bukkit.Tasks
import java.util.*

object NpcStrategyLogic : Runnable {

    private val originalLines: MutableMap<UUID, List<String>> = hashMapOf()

    override fun run() {
        for (npc in EntityHandler.getEntities().filterIsInstance<NpcEntity>()) {
            if (npc.phrases.isNotEmpty()) {
                try {
                    if (System.currentTimeMillis() >= npc.nextPhrase) {
                        originalLines[npc.uuid] = ArrayList(npc.getLines())

                        npc.nextPhrase = npc.randomTime()
                        npc.updateLines(npc.getRandomPhrase())

                        Tasks.asyncDelayed(20L * 5L) {
                            originalLines.remove(npc.uuid).also {
                                if (it != null) {
                                    npc.updateLines(it)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Cubed.instance.logger.severe("Failed to process NPC phrase logic")
                }
            }
        }
    }

    @JvmStatic
    fun restoreOriginalLines() {
        for ((uuid, lines) in originalLines) {
            val entity = EntityHandler.getEntityByUuid(uuid)
            if (entity is NpcEntity) {
                entity.updateLines(lines)
            }
        }
    }

}