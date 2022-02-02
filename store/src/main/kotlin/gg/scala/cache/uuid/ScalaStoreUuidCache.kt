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
    private lateinit var translator: ScalaStoreUuidCacheTranslator

    fun configure(
        translator: ScalaStoreUuidCacheTranslator
    )
    {
        this.translator = translator
        this.translator.configure()
        this.translator.preLoadCache(async = false)

        Logger.getGlobal().info("Loaded UUIDCache with ${translator.javaClass.simpleName}.")
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
        validateTranslator()

        val translated = translator
            .uniqueId(username)

        if (translated != null)
            return translated

        val mojang = ScalaStoreUuidCacheHelper
            .fetchFromMojang(username)

        if (mojang.isPresent)
        {
            val pair = mojang.get()
            translator.update(pair, commit = true)

            return pair.first
        }

        return null
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
        validateTranslator()

        val translated = translator
            .username(uniqueId)

        if (translated != null)
            return translated

        val mojang = ScalaStoreUuidCacheHelper
            .fetchFromMojang(uniqueId)

        if (mojang.isPresent)
        {
            val pair = mojang.get()
            translator.update(pair, commit = true)

            return pair.second
        }

        return null
    }

    private fun validateTranslator()
    {
        try
        {
            translator
        } catch (ignored: Exception)
        {
            translator = LocalUuidCacheTranslator
        }
    }

}
