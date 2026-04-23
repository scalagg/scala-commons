/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.time

import me.lucko.helper.time.DurationParser

class Duration(private var time: Long) {

    fun get(): Long {
        return time
    }

    fun isPermanent(): Boolean {
        return time == Long.MAX_VALUE
    }

    companion object {
        @JvmStatic
        val NOT_PROVIDED = Duration(-1L)

        @JvmStatic
        val PERMANENT = Duration(Long.MAX_VALUE)

        @JvmStatic
        fun parse(input: String): Duration {
            if (input == "NOT_PROVIDED") {
                return NOT_PROVIDED
            }

            if (input.equals("perm", ignoreCase = true) || input.equals("permanent", ignoreCase = true) || input.equals("-s", true)) {
                return PERMANENT
            }

            return Duration(
                DurationParser.parse(input).toMillis()
            )
        }
    }
}
