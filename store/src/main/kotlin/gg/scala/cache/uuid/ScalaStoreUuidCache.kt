package gg.scala.cache.uuid

import gg.scala.cache.uuid.impl.LocalUuidCacheTranslator
import java.util.*
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 2/1/2022
 */
object ScalaStoreUuidCache
{
    private var translator: ScalaStoreUuidCacheTranslator =
        LocalUuidCacheTranslator

    fun configure(
        translator: ScalaStoreUuidCacheTranslator
    )
    {
        this.translator = translator

        Logger.getGlobal().info("Loading UUIDCache with ${translator.javaClass.simpleName}...")

        translator.configure()
            .thenRun {
                ScalaStoreUuidCache.translator.preLoadCache(async = false)

                Logger.getGlobal().info("Loaded UUIDCache with ${translator.javaClass.simpleName}!")
            }
    }

    fun useTranslator(
        lambda: ScalaStoreUuidCacheTranslator.() -> Unit
    )
    {
        lambda.invoke(translator)
    }

    /**
     * Finds a cached uniqueId, or
     * fetches & caches the newly fetched uniqueId.
     *
     * @param username the player username
     * @return a nullable [UUID]
     */
    fun uniqueId(username: String): UUID?
    {
        val translated = translator
            .uniqueId(username)

        if (translated != null)
            return translated

        val response = ScalaStoreUuidCacheHelper
            .find(username)
            ?: return null

        translator.update(
            response.uuid to response.username,
            commit = true
        )

        return response.uuid
    }

    /**
     * Finds a cached username, or
     * fetches & caches the newly fetched username.
     *
     * If the two checks above fail, we will resort to
     *
     * @param uniqueId the player uniqueId
     * @return a nullable [String]
     */
    fun username(uniqueId: UUID): String?
    {
        val translated = translator
            .username(uniqueId)

        if (translated != null)
            return translated

        val response = ScalaStoreUuidCacheHelper
            .find(uniqueId.toString())
            ?: return null

        translator.update(
            response.uuid to response.username,
            commit = true
        )

        return response.username
    }
}
