package net.evilblock.cubed.util.math

import java.text.DecimalFormat

/**
 * @author Limo
 * @date 2/3/26
 */
object Numbers
{
    private val decimalFormat = DecimalFormat("#,###.##")
    private val intFormat = DecimalFormat("#,###")
    private val percentFormat = DecimalFormat("0.0%")
    private val compactFormat = DecimalFormat("0.#")

    fun format(number: Number): String
    {
        return when (number)
        {
            is Double, is Float -> decimalFormat.format(number)
            else -> intFormat.format(number)
        }
    }

    fun format(number: Int): String = intFormat.format(number)

    fun format(number: Long): String = intFormat.format(number)

    fun format(number: Double): String = decimalFormat.format(number)

    fun format(number: Float): String = decimalFormat.format(number)

    fun formatPercent(ratio: Double): String = percentFormat.format(ratio)

    fun formatCompact(number: Long): String
    {
        return when
        {
            number >= 1_000_000_000 -> compactFormat.format(number / 1_000_000_000.0) + "B"
            number >= 1_000_000 -> compactFormat.format(number / 1_000_000.0) + "M"
            number >= 1_000 -> compactFormat.format(number / 1_000.0) + "K"
            else -> number.toString()
        }
    }

    fun formatCompact(number: Int): String = formatCompact(number.toLong())

    fun formatCompact(number: Double): String = formatCompact(number.toLong())

    fun formatOrdinal(number: Int): String
    {
        val suffix = when
        {
            number in 11..13 -> "th"
            number % 10 == 1 -> "st"
            number % 10 == 2 -> "nd"
            number % 10 == 3 -> "rd"
            else -> "th"
        }
        return "$number$suffix"
    }

    fun toRomanNumeral(number: Int): String
    {
        if (number <= 0 || number > 3999) return number.toString()

        val numerals = listOf(
            1000 to "M", 900 to "CM", 500 to "D", 400 to "CD",
            100 to "C", 90 to "XC", 50 to "L", 40 to "XL",
            10 to "X", 9 to "IX", 5 to "V", 4 to "IV", 1 to "I"
        )

        var remaining = number
        val result = StringBuilder()

        for ((value, numeral) in numerals)
        {
            while (remaining >= value)
            {
                result.append(numeral)
                remaining -= value
            }
        }

        return result.toString()
    }

    fun clamp(value: Int, min: Int, max: Int): Int = maxOf(min, minOf(max, value))

    fun clamp(value: Double, min: Double, max: Double): Double = maxOf(min, minOf(max, value))

    fun clamp(value: Float, min: Float, max: Float): Float = maxOf(min, minOf(max, value))

    fun clamp(value: Long, min: Long, max: Long): Long = maxOf(min, minOf(max, value))

    
    fun parseInput(input: String): Number
    {
        val cleaned = input.replace(",", "").trim()

        return if (cleaned.contains("."))
        {
            cleaned.toDouble()
        }
        else
        {
            cleaned.toLongOrNull() ?: cleaned.toDouble()
        }
    }
}
