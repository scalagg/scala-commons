/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.lore

import java.nio.charset.Charset

object HiddenLore {

    private const val COLOR_CHAR = '\u00A7'

    private val SEQUENCE_HEADER = "" + COLOR_CHAR + "r" + COLOR_CHAR + "n" + COLOR_CHAR + "r"
    private val SEQUENCE_FOOTER = "" + COLOR_CHAR + "r" + COLOR_CHAR + "o" + COLOR_CHAR + "r"

    @JvmStatic
    fun encodeString(hiddenString: String): String {
        return quote(stringToColors(hiddenString))
    }

    @JvmStatic
    fun hasHiddenString(input: String): Boolean {
        return input.contains(SEQUENCE_HEADER) && input.contains(SEQUENCE_FOOTER)
    }

    @JvmStatic
    fun extractHiddenString(input: String): String? {
        return colorsToString(extract(input))
    }

    @JvmStatic
    fun replaceHiddenString(input: String, hiddenString: String): String? {
        val start = input.indexOf(SEQUENCE_HEADER)
        val end = input.indexOf(SEQUENCE_FOOTER)
        return if (start < 0 || end < 0) {
            null
        } else input.substring(0, start + SEQUENCE_HEADER.length) + stringToColors(hiddenString) + input.substring(
            end,
            input.length
        )
    }

    @JvmStatic
    private fun quote(input: String): String {
        return SEQUENCE_HEADER + input + SEQUENCE_FOOTER
    }

    @JvmStatic
    private fun extract(input: String): String {
        val start = input.indexOf(SEQUENCE_HEADER)
        val end = input.indexOf(SEQUENCE_FOOTER)
        return input.substring(start + SEQUENCE_HEADER.length, end)
    }

    @JvmStatic
    private fun stringToColors(normal: String): String {
        val bytes = normal.toByteArray(Charset.forName("UTF-8"))
        val chars = CharArray(bytes.size * 4)
        for (i in bytes.indices) {
            val hex = byteToHex(bytes[i])
            chars[i * 4] = COLOR_CHAR
            chars[i * 4 + 1] = hex[0]
            chars[i * 4 + 2] = COLOR_CHAR
            chars[i * 4 + 3] = hex[1]
        }
        return String(chars)
    }

    @JvmStatic
    private fun colorsToString(colors: String): String {
        var colors = colors
        colors = colors.toLowerCase().replace("" + COLOR_CHAR, "")
        if (colors.length % 2 != 0) {
            colors = colors.substring(0, colors.length / 2 * 2)
        }
        val chars = colors.toCharArray()
        val bytes = ByteArray(chars.size / 2)
        var i = 0
        while (i < chars.size) {
            bytes[i / 2] = hexToByte(chars[i], chars[i + 1])
            i += 2
        }
        return String(bytes, Charset.forName("UTF-8"))
    }

    @JvmStatic
    private fun hexToUnsignedInt(c: Char): Int {
        return when (c) {
            in '0'..'9' -> {
                c.toInt() - 48
            }
            in 'a'..'f' -> {
                c.toInt() - 87
            }
            else -> {
                throw IllegalArgumentException("Invalid hex char: out of range")
            }
        }
    }

    @JvmStatic
    private fun unsignedIntToHex(i: Int): Char {
        return when (i) {
            in 0..9 -> {
                (i + 48).toChar()
            }
            in 10..15 -> {
                (i + 87).toChar()
            }
            else -> {
                throw IllegalArgumentException("Invalid hex int: out of range")
            }
        }
    }

    @JvmStatic
    private fun hexToByte(hex1: Char, hex0: Char): Byte {
        return ((hexToUnsignedInt(hex1) shl 4 or hexToUnsignedInt(hex0)) + Byte.MIN_VALUE).toByte()
    }

    @JvmStatic
    private fun byteToHex(b: Byte): CharArray {
        val unsignedByte = b.toInt() - Byte.MIN_VALUE
        return charArrayOf(unsignedIntToHex(unsignedByte shr 4 and 0xf), unsignedIntToHex(unsignedByte and 0xf))
    }
}
