/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.math

import java.math.BigDecimal
import java.math.BigInteger
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.math.round

object Numbers {

    private val NORMAL_FORMAT = DecimalFormat("###.##")
    private val DECIMAL_FORMAT = DecimalFormat("###.##")

    private val LONG_MAX_BIG_INT = BigInteger(Long.MAX_VALUE.toString())
    private val DOUBLE_MAX_BIG_INT = BigInteger(Double.MAX_VALUE.toInt().toString())

    private val LONG_MAX_BIG_DEC = BigDecimal(Long.MAX_VALUE.toString())
    private val DOUBLE_MAX_BIG_DEC = BigDecimal(Double.MAX_VALUE.toInt().toString())

    private val SUFFIXES = TreeMap<Long, String>()

    init {
        DECIMAL_FORMAT.isDecimalSeparatorAlwaysShown = true
        DECIMAL_FORMAT.minimumFractionDigits = 1

        SUFFIXES[1_000L] = "k"
        SUFFIXES[1_000_000L] = "M"
        SUFFIXES[1_000_000_000L] = "B"
        SUFFIXES[1_000_000_000_000L] = "T"
        SUFFIXES[1_000_000_000_000_000L] = "QD"
        SUFFIXES[1_000_000_000_000_000_000L] = "QN"
    }

    @JvmStatic
    fun percentage(value: Int, goal: Int): Double {
        return if (value > goal) {
            100.0
        } else {
            (value.toDouble() / goal.toDouble()) * 100.0
        }
    }

    @JvmStatic
    fun percentage(value: Double, goal: Double): Double {
        return if (value > goal) {
            100.0
        } else {
            (value / goal) * 100.0
        }
    }

    @JvmStatic
    fun percentage(value: Long, goal: Long): Double {
        return if (value > goal) {
            100.0
        } else {
            (value / goal) * 100.0
        }
    }

    @JvmStatic
    fun formatDecimal(double: Double): String {
        return DECIMAL_FORMAT.format(double)
    }

    @JvmStatic
    fun format(number: Number, forceAbbreviation: Boolean = false): String {
        if (number is BigInteger) {
            return when {
                number > LONG_MAX_BIG_INT -> {
                    number.toString()
                }
                number > DOUBLE_MAX_BIG_INT -> {
                    format(number.toLong(), forceAbbreviation)
                }
                else -> {
                    format(number.toDouble(), forceAbbreviation)
                }
            }
        } else if (number is BigDecimal) {
            return when {
                number > LONG_MAX_BIG_DEC -> {
                    number.toString()
                }
                number > DOUBLE_MAX_BIG_DEC -> {
                    format(number.toLong(), forceAbbreviation)
                }
                else -> {
                    format(number.toDouble(), forceAbbreviation)
                }
            }
        }

        return NumberFormat.getInstance().format(number)
    }

    @JvmStatic
    fun format(value: Int, forceAbbreviation: Boolean = false): String {
        return format(value.toLong(), forceAbbreviation)
    }

    @JvmStatic
    fun format(value: Long, forceAbbreviation: Boolean = false): String {
        if (value == java.lang.Long.MIN_VALUE) {
            return format(java.lang.Long.MIN_VALUE + 1)
        }

        if (value < 0) {
            return "-" + format(-value)
        }

        if (value < 1_000) {
            return value.toString()
        }

        if (!forceAbbreviation && value < 1_000_000) {
            return NumberFormat.getInstance().format(value)
        }

        val e = SUFFIXES.floorEntry(value)
        val divideBy = e.key
        val suffix = e.value

        return NORMAL_FORMAT.format((value / (divideBy!! / 10.0)) / 10.0) + suffix
    }

    @JvmStatic
    fun format(value: Double, forceAbbreviation: Boolean = false): String {
        if (value == Double.MIN_VALUE) {
            return format(Double.MIN_VALUE + 1)
        }

        if (value < 0) {
            return "-" + format(-value)
        }

        if (value < 1_000) {
            return value.toString()
        }

        if (!forceAbbreviation && value < 1_000_000) {
            return NumberFormat.getInstance().format(value)
        }

        val e = SUFFIXES.floorEntry(value.toLong())
        val divideBy = e.key
        val suffix = e.value

        return NORMAL_FORMAT.format((value / (divideBy!! / 10.0)) / 10.0) + suffix
    }

    @JvmStatic
    fun parseInput(rawInput: String): Number {
        val input = rawInput.replace(",", "").replace("_", "")

        var matchingSuffix: Pair<Long, String>? = null
        for ((amount, suffix) in SUFFIXES.entries) {
            if (input.endsWith(suffix, ignoreCase = true)) {
                matchingSuffix = amount to suffix
                break
            }
        }

        if (matchingSuffix != null) {
            val filteredInput = input.trim().toLowerCase().replace(matchingSuffix.second, "")

            val hasDecimal = filteredInput.contains(".")
            if (hasDecimal) {
                val numSplit = filteredInput.split(".")
                if (numSplit.size > 2) {
                    throw IllegalStateException("More than one `period` token in input")
                }

                val parsedIntegral = numSplit[0].toLong()
                val parsedFractional = numSplit[1].toFloat()

                return (parsedIntegral * matchingSuffix.first) + (parsedFractional * (matchingSuffix.first / 10.0F))
            }

            val parsedIntegral = NumberFormat.getInstance().parse(filteredInput).toLong()
            return parsedIntegral * matchingSuffix.first
        }

        return NumberFormat.getInstance().parse(input)
    }

    @JvmStatic
    fun isInt(numString: String): Boolean {
        return try {
            numString.toInt()
            true
        } catch (e: Exception) {
            false
        }
    }

    @JvmStatic
    fun numberOperation(n1: Number, n2: Number, add: Boolean): Number {
        return if (n1 is Int && n2 is Int) {
            if (add) {
                n1.toInt() + n2.toInt()
            } else {
                n1.toInt() - n2.toInt()
            }
        } else if (n1 is Double && n2 is Double) {
            if (add) {
                n1.toDouble() + n2.toDouble()
            } else {
                n1.toDouble() - n2.toDouble()
            }
        } else if (n1 is Long && n2 is Long) {
            if (add) {
                n1.toLong() + n2.toLong()
            } else {
                n1.toLong() - n2.toLong()
            }
        } else {
            return BigDecimal(n1.toString()).add(BigDecimal(n2.toString())) // just to be safe
        }
    }

    @JvmStatic
    fun numberToBigInteger(number: Number): BigInteger {
        return when (number) {
            is BigInteger -> {
                return number
            }
            is BigDecimal -> {
                return number.toBigInteger()
            }
            is Float -> {
                BigInteger(round(number).toInt().toString())
            }
            is Double -> {
                BigInteger(round(number).toInt().toString())
            }
            else -> {
                BigInteger(number.toString())
            }
        }
    }

//    @JvmStatic
//    fun numberToBigDecimal(number: Number): BigDecimal {
//        return when (number) {
//            is BigInteger -> {
//                return number.toBigDecimal()
//            }
//            is BigDecimal -> {
//                return number
//            }
//            is Float -> {
//                BigDecimal(number.toString())
//            }
//            is Double -> {
//                BigDecimal(number)
//            }
//            else -> {
//                BigDecimal(number.toString())
//            }
//        }
//    }
//
//    @JvmStatic
//    fun sum(col: Collection<BigInteger>): BigInteger {
//        var sum = BigInteger.ZERO
//        for (i in col) {
//            sum += i
//        }
//        return sum
//    }
//
//    @JvmStatic
//    fun sum(col: Collection<BigDecimal>): BigDecimal {
//        var sum = BigDecimal.ZERO
//        for (i in col) {
//            sum += i
//        }
//        return sum
//    }

}
