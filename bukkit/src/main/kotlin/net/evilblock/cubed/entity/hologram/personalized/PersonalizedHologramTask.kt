package net.evilblock.cubed.entity.hologram.personalized

import gg.scala.commons.annotations.runnables.Repeating
import me.lucko.helper.promise.ThreadContext
import net.evilblock.cubed.entity.hologram.HologramHandler

@Repeating(10L, context = ThreadContext.ASYNC)
object PersonalizedHologramTask : Runnable
{
    override fun run()
    {
        for (hologram in HologramHandler.getPersonalizedHolograms())
        {
            if (hologram.canUpdate())
            {
                hologram.updateLinesForAll()
            }
        }
    }
}
