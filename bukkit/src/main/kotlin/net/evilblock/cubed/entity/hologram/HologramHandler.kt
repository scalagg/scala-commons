/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.hologram

import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.npc.personalized.PersonalizedNpcEntity
import net.evilblock.cubed.entity.hologram.updating.UpdatingHologramEntity

object HologramHandler {

    fun getHolograms(): List<HologramEntity> {
        return EntityHandler.getEntitiesByType(HologramEntity::class.java)
    }

    fun getUpdatingHolograms(): List<UpdatingHologramEntity> {
        return EntityHandler.getEntitiesByType(UpdatingHologramEntity::class.java)
    }

    fun getPersonalizedHolograms(): List<PersonalizedNpcEntity> {
        return EntityHandler.getEntitiesByType(PersonalizedNpcEntity::class.java)
    }

}
