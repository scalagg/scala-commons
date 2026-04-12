

package net.evilblock.cubed.entity.hologram

import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.hologram.personalized.PersonalizedHologramEntity
import net.evilblock.cubed.entity.hologram.updating.UpdatingHologramEntity

object HologramHandler {

    fun getHolograms(): List<HologramEntity> {
        return EntityHandler.getEntitiesByType(HologramEntity::class.java)
    }

    fun getUpdatingHolograms(): List<UpdatingHologramEntity> {
        return EntityHandler.getEntitiesByType(UpdatingHologramEntity::class.java)
    }

    fun getPersonalizedHolograms(): List<PersonalizedHologramEntity> {
        return EntityHandler.getEntitiesByType(PersonalizedHologramEntity::class.java)
    }

}