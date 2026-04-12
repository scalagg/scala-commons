package net.evilblock.cubed.entity.npc.texture

import com.google.gson.JsonParser
import gg.scala.cache.uuid.ScalaStoreUuidCache
import java.net.URL
import java.util.*

data class NpcTexture(val textureValue: String, val textureSignature: String) {

    override fun equals(other: Any?): Boolean {
        return other is NpcTexture && other.textureValue == textureValue && other.textureSignature == textureSignature
    }

    override fun hashCode(): Int {
        var result = textureValue.hashCode()
        result = 31 * result + textureSignature.hashCode()
        return result
    }

    companion object {
        private val parser = JsonParser()

        @JvmStatic
        fun fetchTextureByUsername(username: String): Optional<NpcTexture> {
            val uuid: UUID = ScalaStoreUuidCache.uniqueId(username) ?: return Optional.empty()

            return try {
                val response = runCatching {
                    URL("https://api.mineskin.org/generate/user/$uuid").readText()
                }.getOrNull()

                if (response != null) {
                    val json = parser.parse(response).asJsonObject
                    val data = json.get("data").asJsonObject
                    val texture = data.get("texture").asJsonObject

                    Optional.of(NpcTexture(texture.get("value").asString, texture.get("signature").asString))
                } else {
                    Optional.empty()
                }
            } catch (e: Exception) {
                throw IllegalStateException("Failed to fetch player texture from API", e)
            }
        }
    }
}
