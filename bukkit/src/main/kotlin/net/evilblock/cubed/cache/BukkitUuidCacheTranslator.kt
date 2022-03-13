package net.evilblock.cubed.cache

import gg.scala.cache.uuid.ScalaStoreUuidCacheTranslator
import org.bukkit.Bukkit
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ConcurrentHashMap

/**
 * @author GrowlyX
 * @since 2/1/2022
 */
object BukkitUuidCacheTranslator : ScalaStoreUuidCacheTranslator
{
    override val uniqueIdCache: ConcurrentHashMap<UUID, String>
        get() = TODO("Not yet implemented")
    override val usernameCache: ConcurrentHashMap<String, UUID>
        get() = TODO("Not yet implemented")

    /**
     * An initial startup method for this translator.
     */
    override fun configure(): CompletionStage<Void>
    {
        return CompletableFuture()
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
        return Bukkit.getOfflinePlayer(username)?.uniqueId
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
        return Bukkit.getOfflinePlayer(uniqueId)?.name
    }

}
