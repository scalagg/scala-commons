/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.serializers.impl

import java.lang.reflect.Type

interface AbstractTypeSerializable {

    fun getAbstractType(): Type {
        throw IllegalStateException("Serializable abstract type has not been setup")
    }

}