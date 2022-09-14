package gg.scala.cache.uuid.resolver.impl

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.gson.JsonParser
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import gg.scala.cache.uuid.resolver.DataResolver
import gg.scala.cache.uuid.resolver.ResolverType
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 9/13/2022
 */
object MojangDataResolver : DataResolver
{
    private val parser = JsonParser()
    private val client = OkHttpClient()

    private val invalidCache = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(1L, TimeUnit.MINUTES)
        .build<String, String>()

    override fun assignedTo() = ResolverType.Mojang

    /**
     * Fetches a UUID and Username pair from the Mojang API.
     * Should not be called very often as the server could be rate-limited which will cause more issues.
     */
    @JvmStatic
    @Suppress("DEPRECATION")
    fun fetchFromMojang(username: String): Pair<UUID, String>?
    {
        val invalid = invalidCache
            .getIfPresent(username.lowercase())

        if (invalid != null)
        {
            return null
        }

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

                return uuid to name
            }
        } catch (e: Exception)
        {
            invalidCache.put(username.lowercase(), username)

            Logger.getGlobal().info("Failed to retrieve user info from Mojang API: $username")
            Logger.getGlobal().info("This username will be considered \"invalid\" for the next minute.")
        }

        return null
    }

    @JvmStatic
    fun fetchFromMojang(uuid: UUID): Pair<UUID, String>?
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
                    return uuid to names.first().get("name").asString
                }

                val name = names
                    .filter { it.has("changedToAt") }
                    .maxByOrNull { it.get("changedToAt").asLong }!!.get("name").asString

                return uuid to name
            }
        } catch (e: Exception)
        {
            Logger.getGlobal().info("Failed to retrieve user info from Mojang API: $uuid")
        }

        return null
    }

    override fun uniqueIdToUsername(uniqueId: UUID) =
        fetchFromMojang(uniqueId)

    override fun usernameToUniqueId(username: String) =
        fetchFromMojang(username)
}
