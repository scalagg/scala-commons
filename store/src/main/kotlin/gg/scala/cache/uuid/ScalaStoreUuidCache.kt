package gg.scala.cache.uuid

import gg.scala.cache.uuid.impl.LocalUuidCacheTranslator
import gg.scala.cache.uuid.resolver.DataResolver
import gg.scala.cache.uuid.resolver.ResolverType
import gg.scala.cache.uuid.resolver.impl.AshconDataResolver
import gg.scala.cache.uuid.resolver.impl.MojangDataResolver
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

    lateinit var resolver: DataResolver

    private val registry = mutableMapOf(
        ResolverType.Mojang to MojangDataResolver,
        ResolverType.Ashcon to AshconDataResolver
    )

    fun resolve(resolverType: ResolverType) = this.registry[resolverType]!!

    fun configure(
        translator: ScalaStoreUuidCacheTranslator,
        resolver: DataResolver
    )
    {
        this.translator = translator
        this.resolver = resolver

        Logger.getGlobal().info("Loading UUIDCache with ${translator.javaClass.simpleName} and ${resolver.javaClass.simpleName}.")

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

        val response = this.resolver
            .usernameToUniqueId(username)
            ?: return null

        translator.update(
            response, commit = true
        )

        return response.first
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

        val response = this.resolver
            .uniqueIdToUsername(uniqueId)
            ?: return null

        translator.update(
            response, commit = true
        )

        return response.second
    }
}
