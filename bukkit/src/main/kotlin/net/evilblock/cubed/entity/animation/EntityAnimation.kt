

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