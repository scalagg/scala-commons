package net.evilblock.cubed.entity.npc.texture

import com.google.gson.JsonParser
import gg.scala.cache.uuid.ScalaStoreUuidCache
import java.io.DataOutputStream
import java.net.HttpURLConnection
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

        /**
         * Attempts to fetch a player texture by username.
         * Should not be called very often as the server could be rate-limited which will cause issues.
         */
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

        @JvmStatic
        fun fetchTextureByUrl(username: String): Optional<NpcTexture> {
            return try {
                val response = runCatching {
                    val connection = URL("https://api.mineskin.org/generate/url").openConnection() as HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("User-Agent", "Scala.gg")
                    connection.setRequestProperty("accept", "application/json")
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.doOutput = true

                    val outStream = connection.outputStream
                    val out = DataOutputStream(outStream)
                    out.writeBytes("{\"variant\":\"" + "classic" + "\",\"url\":\"" + username + "\"}");
                    out.flush()
                    out.close()
                    outStream.close()

                    connection.disconnect()
                    connection.inputStream.bufferedReader().use { it.readText() }
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
                throw IllegalStateException("Failed to fetch player texture from URL", e)
            }
        }
    }
}
