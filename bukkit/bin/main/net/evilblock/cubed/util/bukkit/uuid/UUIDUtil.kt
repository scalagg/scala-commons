package net.evilblock.cubed.util.bukkit.uuid

import java.util.*

object UUIDUtil {

    @JvmStatic
    fun formatUUID(id: String): UUID? {
        return UUID.fromString(id.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(), "$1-$2-$3-$4-$5"))
    }
}