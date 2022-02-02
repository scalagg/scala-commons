package gg.scala.cache.uuid.impl

import gg.scala.cache.uuid.ScalaStoreUuidCacheTranslator
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author GrowlyX
 * @since 2/1/2022
 */
object LocalUuidCacheTranslator : ScalaStoreUuidCacheTranslator
{
    override val uniqueIdCache = ConcurrentHashMap<UUID, String>()
    override val usernameCache = ConcurrentHashMap<String, UUID>()

    /**
     * An initial startup method for this translator.
     */
    override fun configure()
    {

    }

    /**
     * Pre-loads the cache through whatever
     * service the implementation is using.
     */
    override fun preLoadCache(async: Boolean)
    {

    }

    /**
     * Updates/commits a UUID, Username pair to whatever
     * service the implementation is using.
     *
     * @param pair the uniqueId, username pair
     */
    override fun update(pair: Pair<UUID, String>, commit: Boolean)
    {
        usernameCache[pair.second.lowercase()] = pair.first
        uniqueIdCache[pair.first] = pair.second
    }

    /**
     * Finds a cached uniqueId, or
     * fetches & caches the newly fetched uniqueId.
     *
     * @param username the player username
     * @return a nullable [UUID]
     */
    override fun uniqueId(username: String): UUID?
    {
        return usernameCache[username.lowercase()]
    }

    /**
     * Finds a cached username, or
     * fetches & caches the newly fetched username.
     *
     * @param uniqueId the player uniqueId
     * @return a nullable [String]
     */
    override fun username(uniqueId: UUID): String?
    {
        return uniqueIdCache[uniqueId]
    }

}
