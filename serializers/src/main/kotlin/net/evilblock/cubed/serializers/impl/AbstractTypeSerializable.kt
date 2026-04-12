

package net.evilblock.cubed.serializers.impl

import java.lang.reflect.Type

interface AbstractTypeSerializable {

    fun getAbstractType(): Type {
        throw IllegalStateException("Serializable abstract type has not been setup")
    }

}