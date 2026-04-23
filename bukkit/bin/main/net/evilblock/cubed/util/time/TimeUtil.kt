package net.evilblock.cubed.util.time

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Time utility functions.
 * 
 * @author Limo
 * @date 2/3/26
 */
object TimeUtil
{
    private val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
    
    fun formatIntoDetailedString(seconds: Long): String
    {
        if (seconds <= 0) return "now"
        
        val days = TimeUnit.SECONDS.toDays(seconds)
        val hours = TimeUnit.SECONDS.toHours(seconds) % 24
        val minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60
        val secs = seconds % 60
        
        val parts = mutableListOf<String>()
        if (days > 0) parts.add("${days}d")
        if (hours > 0) parts.add("${hours}h")
        if (minutes > 0) parts.add("${minutes}m")
        if (secs > 0) parts.add("${secs}s")
        
        return parts.joinToString(" ")
    }

    fun formatIntoDetailedString(seconds: Int): String = formatIntoDetailedString(seconds.toLong())
    
    fun formatIntoAbbreviatedString(seconds: Long): String
    {
        if (seconds <= 0) return "now"
        
        val days = TimeUnit.SECONDS.toDays(seconds)
        val hours = TimeUnit.SECONDS.toHours(seconds) % 24
        val minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60
        val secs = seconds % 60
        
        return when {
            days > 0 -> "${days}d ${hours}h"
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${secs}s"
            else -> "${secs}s"
        }
    }
    
    fun formatIntoAbbreviatedString(seconds: Int): String = formatIntoAbbreviatedString(seconds.toLong())
    
    fun formatMillisIntoAbbreviatedString(millis: Long): String = formatIntoAbbreviatedString(millis / 1000)
    
    fun formatIntoDateString(millis: Long): String = dateFormat.format(Date(millis))
    
    fun formatIntoDateString(date: Date): String = dateFormat.format(date)
    
    fun formatIntoMMSS(seconds: Long): String
    {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
    }
    
    fun formatIntoMMSS(seconds: Int): String = formatIntoMMSS(seconds.toLong())
    
    fun formatDuration(millis: Long): String = formatIntoDetailedString(millis / 1000)
    
    fun parseTime(input: String): Long
    {
        var totalSeconds = 0L
        val regex = "(\\d+)([dhms])".toRegex()
        regex.findAll(input.lowercase()).forEach { match ->
            val value = match.groupValues[1].toLong()
            val unit = match.groupValues[2]
            totalSeconds += when (unit) {
                "d" -> value * 86400
                "h" -> value * 3600
                "m" -> value * 60
                "s" -> value
                else -> 0
            }
        }
        return totalSeconds * 1000
    }

    private val fullCalendarFormat = SimpleDateFormat("MMMM d, yyyy 'at' h:mm a")

    fun formatIntoFullCalendarString(date: Date): String
    {
        return fullCalendarFormat.format(date)
    }

    fun formatIntoFullCalendarString(millis: Long): String
    {
        return fullCalendarFormat.format(Date(millis))
    }

    private val calendarFormat = SimpleDateFormat("MM/dd/yyyy HH:mm")

    fun formatIntoCalendarString(date: Date): String
    {
        return calendarFormat.format(date)
    }

    /**
     * Converts milliseconds into a rounded human-readable time string.
     */
    fun millisToRoundedTime(millis: Long): String
    {
        if (millis <= 0) return "0 seconds"

        val seconds = millis / 1000
        val days = seconds / 86400
        val hours = (seconds % 86400) / 3600
        val minutes = (seconds % 3600) / 60

        return when
        {
            days > 0 -> "$days day${if (days != 1L) "s" else ""}"
            hours > 0 -> "$hours hour${if (hours != 1L) "s" else ""}"
            minutes > 0 -> "$minutes minute${if (minutes != 1L) "s" else ""}"
            else -> "$seconds second${if (seconds != 1L) "s" else ""}"
        }
    }
}
