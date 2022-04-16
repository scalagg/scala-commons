package gg.scala.cache.uuid

import com.google.gson.JsonParser
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import java.util.*
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 2/1/2022
 */
@Suppress("DEPRECATION")
object ScalaStoreUuidCacheHelper
{
    private val parser = JsonParser()
    private val client = OkHttpClient()

    /**
     * Fetches a UUID and Username pair from the Mojang API.
     * Should not be called very often as the server could be rate-limited which will cause more issues.
     */
    @JvmStatic
    @Suppress("DEPRECATION")
    fun fetchFromMojang(username: String): Optional<Pair<UUID, String>>
    {
        try
        {
            val request = Request.Builder().url("https://api.mojang.com/users/profiles/minecraft/$username").build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful)
            {
                val json = parser.parse(response.body().string()).asJsonObject

                val uuid = UUID.fromString(
                    json.get("id").asString.replaceFirst(
                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(),
                        "$1-$2-$3-$4-$5"
                    )
                )
                val name = json.get("name").asString

                return Optional.of(Pair(uuid, name))
            }
        } catch (e: Exception)
        {
            Logger.getGlobal().info("Failed to retrieve user info from Mojang API: $username")
        }

        return Optional.ofNullable(null)
    }

    @JvmStatic
    fun fetchFromMojang(uuid: UUID): Optional<Pair<UUID, String>>
    {
        try
        {
            val request = Request.Builder()
                .url("https://api.mojang.com/user/profiles/${uuid.toString().replace("-", "")}/names").build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful)
            {
                val names = parser.parse(response.body().string()).asJsonArray.map { it.asJsonObject }
                if (names.size == 1)
                {
                    return Optional.of(uuid to names.first().get("name").asString)
                }

                val name = names
                    .filter { it.has("changedToAt") }
                    .maxByOrNull { it.get("changedToAt").asLong }!!.get("name").asString

                return Optional.of(Pair(uuid, name))
            }
        } catch (e: Exception)
        {
            Logger.getGlobal().info("Failed to retrieve user info from Mojang API: $uuid")
        }

        return Optional.ofNullable(null)
    }
}
