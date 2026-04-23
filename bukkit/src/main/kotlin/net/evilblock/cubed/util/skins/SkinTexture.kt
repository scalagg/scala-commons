package net.evilblock.cubed.util.skins

import com.mojang.authlib.properties.Property
import org.bukkit.entity.Player

data class SkinTexture(
    var value: String,
    var signature: String
)
{
    fun property() = Property("textures", value, signature)

    override fun toString(): String
    {
        return signature + value
    }

    companion object
    {
        fun fromPlayer(player: Player): SkinTexture
        {
            return try {
                val handle = player.javaClass.getMethod("getHandle").invoke(player)
                val profile = handle.javaClass.getMethod("getGameProfile").invoke(handle)
                val properties = profile.javaClass.getMethod("getProperties").invoke(profile)
                val textures = properties.javaClass.getMethod("get", Any::class.java).invoke(properties, "textures") as Iterable<*>
                val property = textures.firstOrNull()
                    ?: error("No textures property is present")

                val value = property.javaClass.getMethod("getValue").invoke(property) as String
                val signature = property.javaClass.getMethod("getSignature").invoke(property) as String
                SkinTexture(value, signature)
            } catch (exception: Exception) {
                throw IllegalStateException("Unable to read player skin texture", exception)
            }
        }
    }
}
