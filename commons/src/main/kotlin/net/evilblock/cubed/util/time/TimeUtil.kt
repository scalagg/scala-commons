/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.time

import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object TimeUtil {

    private val MM_SS_BUILDER: ThreadLocal<StringBuilder> = ThreadLocal.withInitial { java.lang.StringBuilder() }
    private val dateTimeFullFormat: SimpleDateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm aa")
    private val dateTimeFormat: SimpleDateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm")
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("MM/dd/yyyy")

    @JvmStatic
    fun millisToSeconds(millis: Long): String {
        return String.format("%.0f", millis)
    }

    @JvmStatic
    fun formatIntoMMSS(secs: Int): String {
        var secs = secs
        val seconds = secs % 60
        secs -= seconds

        var minutesCount = (secs / 60).toLong()
        val minutes = minutesCount % 60L
        minutesCount -= minutes

        val hours = minutesCount / 60L

        val result = MM_SS_BUILDER.get()
        result.setLength(0)

        if (hours > 0L) {
            if (hours < 10L) {
                result.append("0")
            }

            result.append(hours)
            result.append(":")
        }

        if (minutes < 10L) {
            result.append("0")
        }

        result.append(minutes)
        result.append(":")

        if (seconds < 10) {
            result.append("0")
        }

        result.append(seconds)

        return result.toString()
    }

    @JvmStatic
    fun formatIntoDetailedString(secs: Int): String {
        if (secs == 0) {
            return "0 seconds"
        }
        val remainder = secs % 86400
        val days = secs / 86400
        val hours = remainder / 3600
        val minutes = remainder / 60 - hours * 60
        val seconds = remainder % 3600 - minutes * 60
        val fDays = if (days > 0) " " + days + " day" + if (days > 1) "s" else "" else ""
        val fHours = if (hours > 0) " " + hours + " hour" + if (hours > 1) "s" else "" else ""
        val fMinutes = if (minutes > 0) " " + minutes + " minute" + if (minutes > 1) "s" else "" else ""
        val fSeconds = if (seconds > 0) " " + seconds + " second" + if (seconds > 1) "s" else "" else ""
        return (fDays + fHours + fMinutes + fSeconds).trim { it <= ' ' }
    }

    @JvmStatic
    fun formatMillisIntoAbbreviatedString(millis: Long) = formatIntoAbbreviatedString(millis / 1000L)

    @JvmStatic
    fun formatIntoAbbreviatedString(secs: Long) {
        // ...
    }

    @JvmStatic
    fun formatIntoAbbreviatedString(secs: Int): String {
        if (secs == 0) {
            return "0s"
        }

        val remainder = secs % 86400
        val days = secs / 86400
        val hours = remainder / 3600
        val minutes = remainder / 60 - hours * 60
        val seconds = remainder % 3600 - minutes * 60

        val fDays = if (days > 0) {
            " " + days + "d"
        } else {
            ""
        }

        val fHours = if (hours > 0) {
            " " + hours + "h"
        } else {
            ""
        }

        val fMinutes = if (minutes > 0) {
            " " + minutes + "m"
        } else {
            ""
        }

        val fSeconds = if (seconds > 0) {
            " " + seconds + "s"
        } else {
            ""
        }

        return (fDays + fHours + fMinutes + fSeconds).trim { it <= ' ' }
    }

    @JvmStatic
    fun formatIntoFullCalendarString(date: Date): String {
        return dateTimeFullFormat.format(date)
    }

    @JvmStatic
    fun formatIntoCalendarString(date: Date): String {
        return dateTimeFormat.format(date)
    }

    @JvmStatic
    fun formatIntoDateString(date: Date): String {
        return dateFormat.format(date)
    }

    @JvmStatic
    fun parseTime(time: String): Int {
        if (time == "0" || time == "") {
            return 0
        }

        val lifeMatch = arrayOf("y", "w", "d", "h", "m", "s")
        val lifeInterval = intArrayOf(31_536_000, 604800, 86400, 3600, 60, 1)

        var seconds = -1
        for (i in lifeMatch.indices) {
            val matcher = Pattern.compile("([0-9]+)" + lifeMatch[i]).matcher(time)
            while (matcher.find()) {
                if (seconds == -1) {
                    seconds = 0
                }
                seconds += Integer.parseInt(matcher.group(1)) * lifeInterval[i]
            }
        }

        if (seconds == -1) {
            throw IllegalArgumentException("Invalid time provided.")
        }

        return seconds
    }

    @JvmStatic
    fun millisToRoundedTime(millis: Long): String {
        if (millis == Long.MAX_VALUE) {
            return "Permanent"
        }

        val adjustedMillis = millis + 1
        val seconds = adjustedMillis / 1000L
        val minutes = seconds / 60L
        val hours = minutes / 60L
        val days = hours / 24L
        val weeks = days / 7L
        val months = weeks / 4L
        val years = months / 12L

        return when {
            years > 0L -> "$years year${if (years == 1L) "" else "s"}"
            months > 0L -> "$months month${if (months == 1L) "" else "s"}"
            weeks > 0L -> "$weeks week${if (weeks == 1L) "" else "s"}"
            days > 0L -> "$days day${if (days == 1L) "" else "s"}"
            hours > 0L -> "$hours hour${if (hours == 1L) "" else "s"}"
            minutes > 0L -> "$minutes minute${if (minutes == 1L) "" else "s"}"
            else -> "$seconds second${if (seconds == 1L) "" else "s"}"
        }
    }

    @JvmStatic
    fun elapsed(from: Long, required: Long): Boolean {
        return System.currentTimeMillis() - from > required
    }

    @JvmStatic
    fun elapsed(starttime: Long): Long {
        return System.currentTimeMillis() - starttime
    }

    @JvmStatic
    fun left(start: Long, required: Long): Long {
        return required + start - System.currentTimeMillis()
    }

}
