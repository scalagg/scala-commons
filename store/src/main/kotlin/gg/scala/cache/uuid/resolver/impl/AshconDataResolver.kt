package gg.scala.cache.uuid.resolver.impl

import com.github.benmanes.caffeine.cache.Caffeine
import gg.scala.cache.uuid.AshconResponse
import gg.scala.cache.uuid.resolver.DataResolver
import gg.scala.cache.uuid.resolver.ResolverType
import net.evilblock.cubed.serializers.Serializers
import java.io.FileNotFoundException
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 9/13/2022
 */
object AshconDataResolver : DataResolver
{
    private val invalidCache = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(1L, TimeUnit.MINUTES)
        .build<String, String>()

    override fun assignedTo() = ResolverType.Ashcon

    @JvmStatic
    fun find(uriPart: String): AshconResponse?
    {
        val invalid = invalidCache
            .getIfPresent(
                uriPart.lowercase()
            )

        if (invalid != null)
        {
            return null
        }

        return kotlin
            .runCatching {
                val response = Serializers
                    .gson.fromJson(
                        String(
                            URL("https://api.ashcon.app/mojang/v2/user/$uriPart").openStream().readBytes()
                        ),
                        AshconResponse::class.java
                    )

                if (response.code != null)
                    throw IllegalArgumentException(
                        "Invalid uriPart provided: ${response.reason}"
                    )

                return@runCatching response
            }
            .onFailure {
                invalidCache.put(uriPart, "")

                if (it is FileNotFoundException)
                {
                    it.printStackTrace()
                }
            }
            .getOrNull()
    }

    override fun uniqueIdToUsername(uniqueId: UUID) =
        find(uniqueId.toString())?.let {
            it.uuid to it.username
        }

    override fun usernameToUniqueId(username: String) =
        find(username)?.let {
            it.uuid to it.username
        }
}
