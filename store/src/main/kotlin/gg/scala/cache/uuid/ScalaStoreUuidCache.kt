package gg.scala.cache.uuid

import gg.scala.cache.uuid.impl.LocalUuidCacheTranslator
import gg.scala.cache.uuid.resolver.DataResolver
import gg.scala.cache.uuid.resolver.ResolverType
import gg.scala.cache.uuid.resolver.impl.AshconDataResolver
import gg.scala.cache.uuid.resolver.impl.MojangDataResolver
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

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

                Logger.getGlobal().info(
                    "Loaded UUIDCache with ${translator.javaClass.simpleName}!"
                )
            }
            .exceptionally {
                Logger.getGlobal().log(
                    Level.SEVERE, "Could not pre-load cache", it
                )
                return@exceptionally null
            }
    }

    fun useTranslator(
        lambda: ScalaStoreUuidCacheTranslator.() -> Unit
    )
    {
        lambda.invoke(translator)
    }

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
