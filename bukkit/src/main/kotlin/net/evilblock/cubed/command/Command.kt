/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Command(val names: Array<String>,
                         val permission: String = "",
                         val hidden: Boolean = false,
                         val async: Boolean = false,
                         val description: String = "",
                         val logToConsole: Boolean = true)
