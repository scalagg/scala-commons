package gg.scala.cache.uuid.resolver.impl

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.gson.JsonParser
import gg.scala.cache.uuid.resolver.DataResolver
import gg.scala.cache.uuid.resolver.ResolverType
import java.lang.IllegalStateException
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object MojangDataResolver : DataResolver
{
    private val invalidCache = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(1L, TimeUnit.MINUTES)
        .build<String, String>()

    override fun assignedTo() = ResolverType.Mojang

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
            val url = URL(
                "https://api.mojang.com/users/profiles/minecraft/$username"
            )

            val text = url.readText(charset = Charsets.UTF_8)

            val parsed = JsonParser.parseString(text)
                ?: throw IllegalStateException("Invalid JSON text returned for username- $username")

            val jsonObject = parsed.asJsonObject

            val uniqueId = UUID.fromString(
                jsonObject.get("id").asString
                    .replaceFirst(
                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(),
                        "$1-$2-$3-$4-$5"
                    )
            )

            return uniqueId to jsonObject.get("name").asString
        } catch (exception: Exception)
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
            val url = URL(
                "https://sessionserver.mojang.com/session/minecraft/profile/$uuid"
            )

            val text = url.readText(charset = Charsets.UTF_8)

            val parsed = JsonParser.parseString(text)
                ?: throw IllegalStateException("Invalid JSON text returned for UUID- $uuid")

            val jsonObject = parsed.asJsonObject
            return uuid to jsonObject.get("name").asString
        } catch (exception: Exception)
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
