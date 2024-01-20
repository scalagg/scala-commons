/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.text

import java.util.regex.Pattern

object TextSplitter {

    private val STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '\u00A7'.toString() + "[0-9A-FK-OR]")

    @JvmStatic
    fun stripColor(input: String): String {
        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("")
    }

    @JvmStatic
    fun split(text: String, linePrefix: String, wordSuffix: String): List<String> {
        return split(32, 32, text, linePrefix, wordSuffix)
    }

    @JvmStatic
    fun split(length: Int = 32, firstLineLength: Int = length, text: String, linePrefix: String = "\u00A77", wordSuffix: String = " "): List<String> {
        if (text.length <= length) {
            return arrayListOf(linePrefix + text)
        }

        val lines = ArrayList<String>()
        val split = text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var builder = StringBuilder(linePrefix)

        splitLoop@ for (i in split.indices) {
            val lineLength = if (i == 0) { firstLineLength } else { length }
            val sequenceAtIndex = split[i]
            val sequenceAtIndexLen = stripColor(
                sequenceAtIndex
            ).length
            if (sequenceAtIndexLen > lineLength) {
                var remainingSequence: String = sequenceAtIndex
                while (stripColor(remainingSequence).length >= lineLength) {
                    val substring = remainingSequence.substring(0, lineLength)
                    remainingSequence = remainingSequence.substring(lineLength - 1, remainingSequence.length)
                    lines.add(substring)
                }

                if (remainingSequence.isNotEmpty()) {
                    builder.append(remainingSequence)
                    builder.append(wordSuffix)
                }

                continue@splitLoop
            }

            if (builder.length + sequenceAtIndexLen >= lineLength) {
                lines.add(builder.toString())
                builder = StringBuilder(linePrefix)
            }

            builder.append(sequenceAtIndex)
            builder.append(wordSuffix)

            if (i == split.size - 1) {
                builder.replace(builder.length - wordSuffix.length, builder.length, "")
            }
        }

        if (builder.isNotEmpty()) {
            lines.add(builder.toString())
        }

        return lines
    }

}
