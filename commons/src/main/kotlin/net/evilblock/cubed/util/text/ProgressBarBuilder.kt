/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.text

import java.util.Arrays
import org.apache.commons.lang3.StringUtils

class ProgressBarBuilder(
    private val steps: Int = 10,
    private var char: Char = '⬛',
    private var completedColor: String = "§a§l",
    private var uncompletedColor: String = "§e§l"
)
{
    fun build(percentage: Double): String
    {
        var percentage = percentage

        val steps = arrayOfNulls<String>(this.steps)
        Arrays.fill(steps, uncompletedColor + char)

        if (percentage > 100.0)
        {
            percentage = 100.0
        }

        val percentagePerStep = 100.0 / this.steps.toDouble()

        var i = 0
        while (i * percentagePerStep < percentage)
        {
            if (i > steps.size - 1)
            {
                break
            }

            if (i == steps.size - 1)
            {
                if (percentage < 100.0)
                {
                    break
                }
            }

            if (i > 0)
            {
                if (steps[i - 1]!!.startsWith(completedColor))
                {
                    steps[i++] = char.toString()
                } else
                {
                    steps[i++] = completedColor + char
                }
            } else
            {
                steps[i++] = completedColor + char
            }
        }

        return StringUtils.join(steps)
    }

    companion object
    {
        val DEFAULT = ProgressBarBuilder()

        @JvmStatic
        fun percentage(value: Int, goal: Int): Double
        {
            return if (value > goal) 100.0 else value.toDouble() / goal.toDouble() * 100.0
        }
    }
}
