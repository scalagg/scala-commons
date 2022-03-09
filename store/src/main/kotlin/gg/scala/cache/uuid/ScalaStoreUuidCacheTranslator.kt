package gg.scala.cache.uuid

import java.util.*
import java.util.concurrent.CompletionStage
import java.util.concurrent.ConcurrentHashMap

/**
 * @author GrowlyX
 * @since 2/1/2022
 */
interface ScalaStoreUuidCacheTranslator
{
    val uniqueIdCache: ConcurrentHashMap<UUID, String>
    val usernameCache: ConcurrentHashMap<String, UUID>

    /**
     * An initial startup method for this translator.
     */
    fun configure(): CompletionStage<Void>

    /**
     * Pre-loads the cache through whatever
     * service the implementation is using.
     */
    fun preLoadCache(async: Boolean = true)

    /**
     * Updates/commits a UUID, Username pair to whatever
     * service the implementation is using.
     *
     * @param pair the uniqueId, username pair
     */
    fun update(pair: Pair<UUID, String>, commit: Boolean = true)

    /**
     * Finds a cached uniqueId, or
     * fetches & caches the newly fetched uniqueId.
     *
     * @param username the player username
     * @return a nullable [UUID]
     */
    fun uniqueId(username: String): UUID?

    /**
     * Finds a cached username, or
     * fetches & caches the newly fetched username.
     *
     * @param uniqueId the player uniqueId
     * @return a nullable [String]
     */
    fun username(uniqueId: UUID): String?
}
