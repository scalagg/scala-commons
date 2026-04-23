/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.animation

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable

abstract class EntityAnimation(@Transient internal var entity: Entity) : AbstractTypeSerializable {

    var current: Long = 0L
    var lastTick: Long? = null

    abstract fun getAnimationType(): EntityAnimationType

    abstract fun getInterval(): Long

    abstract fun tick()

}