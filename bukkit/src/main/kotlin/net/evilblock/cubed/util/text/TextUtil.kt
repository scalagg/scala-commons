

package net.evilblock.cubed.util.text

import gg.scala.commons.replacement.Replacements
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.lang.StringBuilder
import java.text.DecimalFormat

object TextUtil {

    private const val CENTER_PX: Int = 154
    private val VOWELS = listOf('a', 'o', 'u', 'i', 'e')
    private val TPS_FORMAT = DecimalFormat("0.00")

    @JvmStatic
    fun formatTPS(tps: Double): String {
        return TPS_FORMAT.format(tps)
    }

    @JvmStatic
    fun colorPercentage(percentage: Double): ChatColor {
        return when {
            percentage >= 90.0 -> {
                ChatColor.DARK_GREEN
            }
            percentage >= 80.0 -> {
                ChatColor.GREEN
            }
            percentage >= 60.0 -> {
                ChatColor.YELLOW
            }
            percentage >= 35.0 -> {
                ChatColor.RED
            }
            else -> {
                ChatColor.DARK_RED
            }
        }
    }

    @JvmStatic
    fun pluralize(num: Int, singular: String, plural: String = singular + "s"): String {
        return if (num == 1) {
            singular
        } else {
            plural
        }
    }

    @JvmStatic
    fun areOrIs(num: Int): String {
        return if (num == 1) {
            "is"
        } else {
            "are"
        }
    }

    @JvmStatic
    fun aOrAn(text: String): String {
        if (text.isEmpty()) {
            return "a $text"
        }

        return if (VOWELS.contains(text.toLowerCase()[0])) {
            "an $text"
        } else {
            "a $text"
        }
    }

    @JvmStatic
    fun isBlank(text: String): Boolean {
        return text.equals("blank", ignoreCase = true) || text.equals("empty", ignoreCase = true) || text.isEmpty() || text.isBlank()
    }

    @JvmStatic
    fun trimIfLonger(text: String, lengthThreshold: Int): String {
        return if (text.length > lengthThreshold) {
            text.substring(0, lengthThreshold) + "..."
        } else {
            text
        }
    }

    @JvmStatic
    fun sendCenteredMessage(player: Player, message: String) {
        if (message.isEmpty() || message.isBlank()) {
            player.sendMessage("")
            return
        }

        var message = Replacements.apply(
            ChatColor.translateAlternateColorCodes('&', message), player
        )

        var messagePxSize = 0
        var previousCode = false
        var isBold = false

        for (c in message.toCharArray()) {
            if (c == '\u00A7') {
                previousCode = true
                continue
            } else if (previousCode) {
                previousCode = false

                if (c == 'l' || c == 'L') {
                    isBold = true
                    continue
                } else {
                    isBold = false
                }
            } else {
                val dFI: DefaultFontInfo = DefaultFontInfo.getDefaultFontInfo(c)
                messagePxSize += if (isBold) dFI.boldLength else dFI.length
                messagePxSize++
            }
        }

        val halvedMessageSize = messagePxSize / 2
        val toCompensate = CENTER_PX - halvedMessageSize
        val spaceLength: Int = DefaultFontInfo.SPACE.length + 1
        var compensated = 0

        val sb = StringBuilder()
        while (compensated < toCompensate) {
            sb.append(" ")
            compensated += spaceLength
        }

        player.sendMessage(sb.toString() + message)
    }

    @JvmStatic
    fun getCentered(message: String): String {
        if (message.isEmpty() || message.isBlank()) {
            return ""
        }

        var message = ChatColor.translateAlternateColorCodes('&', message)
        var messagePxSize = 0
        var previousCode = false
        var isBold = false

        for (c in message.toCharArray()) {
            if (c == '\u00A7') {
                previousCode = true
                continue
            } else if (previousCode) {
                previousCode = false

                if (c == 'l' || c == 'L') {
                    isBold = true
                    continue
                } else {
                    isBold = false
                }
            } else {
                val dFI: DefaultFontInfo = DefaultFontInfo.getDefaultFontInfo(c)
                messagePxSize += if (isBold) dFI.boldLength else dFI.length
                messagePxSize++
            }
        }

        val halvedMessageSize = messagePxSize / 2
        val toCompensate = CENTER_PX - halvedMessageSize
        val spaceLength: Int = DefaultFontInfo.SPACE.length + 1
        var compensated = 0

        val sb = StringBuilder()
        while (compensated < toCompensate) {
            sb.append(" ")
            compensated += spaceLength
        }

        return sb.toString() + message
    }

}
