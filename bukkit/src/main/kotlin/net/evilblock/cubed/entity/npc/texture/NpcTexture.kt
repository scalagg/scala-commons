package net.evilblock.cubed.entity.npc.texture

import com.google.gson.JsonParser
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import net.evilblock.cubed.Cubed
import java.lang.IllegalStateException
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
        private val client = OkHttpClient()
        private val parser = JsonParser()

        /**
         * Attempts to fetch a player texture by username.
         * Should not be called very often as the server could be rate-limited which will cause issues.
         */
        @JvmStatic
        fun fetchTextureByUsername(username: String): Optional<NpcTexture> {
            var uuid = Cubed.instance.uuidCache.uuid(username)
            if (uuid == null) {
                val apiResponse = UUIDCache.fetchFromMojang(username)
                if (!apiResponse.isPresent) {
                    return Optional.empty()
                }

                uuid = apiResponse.get().first
            }

            return try {
                val request = Request.Builder().url("http://api.mineskin.org/generate/user/$uuid").build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val json = parser.parse(response.body().string()).asJsonObject
                    val data = json.get("data").asJsonObject
                    val texture = data.get("texture").asJsonObject

                    response.body().close()

                    Optional.of(NpcTexture(texture.get("value").asString, texture.get("signature").asString))
                } else {
                    response.body().close()

                    Optional.empty()
                }
            } catch (e: Exception) {
                throw IllegalStateException("Failed to fetch player texture from API", e)
            }
        }
    }
}