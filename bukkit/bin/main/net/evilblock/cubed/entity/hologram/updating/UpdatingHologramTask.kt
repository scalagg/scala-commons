package net.evilblock.cubed.entity.hologram.updating

import gg.scala.commons.annotations.runnables.Repeating
import me.lucko.helper.promise.ThreadContext
import net.evilblock.cubed.entity.hologram.HologramHandler

@Repeating(10L, context = ThreadContext.ASYNC)
object UpdatingHologramTask : Runnable
{
    override fun run()
    {
        for (hologram in HologramHandler.getUpdatingHolograms())
        {
            if (!hologram.isInitialized())
            {
                continue
            }

            if (hologram.canUpdate())
            {
                hologram.updateLines(hologram.getNewLines())
                hologram.lastUpdate = System.currentTimeMillis()
            }
        }
    }
}
