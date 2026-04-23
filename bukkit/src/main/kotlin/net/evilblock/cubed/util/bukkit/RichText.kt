package net.evilblock.cubed.util.bukkit

import net.evilblock.cubed.util.Color
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.ChatColor
import java.util.Locale

private val miniMessage = MiniMessage.miniMessage()
private val richTextSerializer = LegacyComponentSerializer.legacySection().toBuilder()
    .useUnusualXRepeatedCharacterHexFormat()
    .extractUrls()
    .build()

fun String.formatRichText(): String
{
    val enumColor = kotlin.runCatching {
        ChatColor.valueOf(trim().uppercase(Locale.getDefault())).toString()
    }.getOrNull()
    if (enumColor != null) {
        return enumColor
    }

    val translated = Color.translate(this)
    if ('<' !in this || '>' !in this) {
        return translated
    }

    return runCatching {
        richTextSerializer.serialize(miniMessage.deserialize(this))
    }.getOrElse {
        translated
    }
}
