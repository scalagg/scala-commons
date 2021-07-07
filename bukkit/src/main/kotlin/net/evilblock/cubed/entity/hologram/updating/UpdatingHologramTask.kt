package net.evilblock.cubed.entity.hologram.updating

import net.evilblock.cubed.entity.hologram.HologramHandler

object UpdatingHologramTask : Runnable {

    override fun run() {
        for (hologram in HologramHandler.getUpdatingHolograms()) {
            if (!hologram.isInitialized()) {
                continue
            }

            if (hologram.canUpdate()) {
                hologram.updateLines(hologram.getNewLines())
                hologram.lastUpdate = System.currentTimeMillis()
            }
        }
    }

}