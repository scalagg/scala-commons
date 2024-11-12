package net.evilblock.cubed.entity.npc.personalized

import gg.scala.commons.annotations.runnables.Repeating
import me.lucko.helper.promise.ThreadContext
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.hologram.HologramHandler

@Repeating(10L, context = ThreadContext.ASYNC)
object PersonalizedNpcTask : Runnable
{
    override fun run()
    {
        for (npcEntity in EntityHandler.getEntitiesOfType<PersonalizedNpcEntity>())
        {
            if (npcEntity.canUpdate())
            {
                npcEntity.updateLinesForAll()
            }
        }
    }
}
