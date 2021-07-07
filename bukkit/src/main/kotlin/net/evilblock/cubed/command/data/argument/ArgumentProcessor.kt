/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command.data.argument

import net.evilblock.cubed.command.data.flag.Flag
import net.evilblock.cubed.command.data.processor.Processor
import java.util.ArrayList

object ArgumentProcessor : Processor<Array<String>, Arguments> {

    override fun process(args: Array<String>): Arguments {
        val flags = ArrayList<String>()
        val arguments = ArrayList<String>()

        for (s in args) {
            if (s.isNotEmpty()) {
                if (s[0] == '-' && s != "-" && matches(s)) {
                    val flag = getFlagName(s)
                    if (flag != null) {
                        flags.add(flag)
                    }
                } else {
                    arguments.add(s)
                }
            }
        }

        return Arguments(arguments, flags)
    }

    private fun getFlagName(flag: String): String? {
        val matcher = Flag.FLAG_PATTERN.matcher(flag)

        if (matcher.matches()) {
            val name = matcher.replaceAll("$2$3")
            return if (name.length == 1) name else name.toLowerCase()
        }

        return null
    }

    private fun matches(flag: String): Boolean {
        return Flag.FLAG_PATTERN.matcher(flag).matches()
    }

}