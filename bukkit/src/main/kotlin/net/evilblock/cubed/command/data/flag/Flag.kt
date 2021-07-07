/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command.data.flag

import java.util.regex.Pattern

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Flag(vararg val value: String, val defaultValue: Boolean = false, val description: String = "") {

    companion object {
        val FLAG_PATTERN: Pattern = Pattern.compile("(-)([a-zA-Z])([\\w]*)?")
    }

}