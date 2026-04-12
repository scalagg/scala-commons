

package net.evilblock.cubed.entity.npc.strategy

import gg.scala.commons.annotations.runnables.Repeating
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Close
import gg.scala.flavor.service.Service
import me.lucko.helper.promise.ThreadContext
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.util.bukkit.Tasks
import java.util.*

@Service
@Repeating(20L, context = ThreadContext.ASYNC)
object NpcStrategyLogic : Runnable
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    private val originalLines: MutableMap<UUID, List<String>> = hashMapOf()

    override fun run()
    {
        for (npc in EntityHandler.getEntities().filterIsInstance<NpcEntity>())
        {
            if (npc.phrases.isNotEmpty())
            {
                try
                {
                    if (System.currentTimeMillis() >= npc.nextPhrase)
                    {
                        originalLines[npc.uuid] = ArrayList(npc.getLines())

                        npc.nextPhrase = npc.randomTime()
                        npc.updateLines(npc.getRandomPhrase())

                        Tasks.asyncDelayed(20L * 5L) {
                            originalLines.remove(npc.uuid).also {
                                if (it != null)
                                {
                                    npc.updateLines(it)
                                }
                            }
                        }
                    }
                } catch (e: Exception)
                {
                    plugin.logger.severe("Failed to process NPC phrase logic")
                }
            }
        }
    }

    @Close
    fun close()
    {
        for ((uuid, lines) in originalLines)
        {
            val entity = EntityHandler.getEntityByUuid(uuid)
            if (entity is NpcEntity)
            {
                entity.updateLines(lines)
            }
        }
    }

}
