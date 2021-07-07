package net.evilblock.cubed.entity.hologram.personalized

import net.evilblock.cubed.entity.hologram.HologramHandler

object PersonalizedHologramTask : Runnable {

    override fun run() {
        for (hologram in HologramHandler.getPersonalizedHolograms()) {
            if (hologram.canUpdate()) {
                hologram.updateLinesForAll()
            }
        }
    }

}