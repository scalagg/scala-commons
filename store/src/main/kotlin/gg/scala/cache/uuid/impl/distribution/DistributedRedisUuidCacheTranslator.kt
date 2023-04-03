package gg.scala.cache.uuid.impl.distribution

import gg.scala.aware.Aware
import gg.scala.aware.message.AwareMessage
import gg.scala.cache.uuid.ScalaStoreUuidCacheTranslator
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.internal.AbstractInvocationHandler
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ForkJoinPool
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Commits the UUID, String [Pair]
 * to our redis cache.
 *
 * Usernames as [String] form in pairs
 * are scanned as their lowercase form
 * to prevent us from encountering any
 * issues while finding a match for the
 * username in our [usernameCache] map.
 *
 * @author GrowlyX
 * @since 2/1/2022
 */
class DistributedRedisUuidCacheTranslator(
    private val aware: Aware<AwareMessage>
) : ScalaStoreUuidCacheTranslator
{
    companion object
    {
        const val USERNAME_REDIS_LOC = "DataStore:UuidCache:Username"
        const val UNIQUE_ID_REDIS_LOC = "DataStore:UuidCache:UUID"
    }

    override val uniqueIdCache = mutableMapOf<UUID, String>()
    override val usernameCache = mutableMapOf<String, UUID>()

    private lateinit var connection:
            StatefulRedisConnection<String, String>

    /**
     * An initial startup method for this translator.
     */
    override fun configure(): CompletionStage<Void>
    {
        return this.aware.connect()
            .thenRun {
                connection = aware
                    .internal().connect()
            }
            .exceptionally {
                it.printStackTrace()
                return@exceptionally null
            }
    }

    override fun close()
    {
        this.aware.shutdown()
    }

    /**
     * Pre-loads the cache through whatever
     * service the implementation is using.
     */
    override fun preLoadCache(async: Boolean)
    {

    }

    /**
     * Commits a UUID, Username pair to whatever
     * service the implementation is using.
     *
     * @param pair the uniqueId, username pair
     */
    override fun update(
        pair: Pair<UUID, String>, commit: Boolean
    )
    {
        if (commit)
        {
            CompletableFuture
                .runAsync {
                    useAndReturn {
                        sync().apply {
                            hset(USERNAME_REDIS_LOC, pair.second.lowercase(), pair.first.toString())
                            hset(UNIQUE_ID_REDIS_LOC, pair.first.toString(), pair.second)
                        }
                    }
                }
        }
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
        return useAndReturn {
            val nullable = this.sync()
                .hget(USERNAME_REDIS_LOC, username.lowercase())
                ?: return@useAndReturn null

            UUID.fromString(nullable)
        }
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
        return useAndReturn {
            sync().hget(UNIQUE_ID_REDIS_LOC, uniqueId.toString())
        }
    }

    private fun <T> useAndReturn(
        lambda: StatefulRedisConnection<String, String>.() -> T?
    ): T?
    {
        return try
        {
            lambda.invoke(connection)
        } catch (exception: Throwable)
        {
            Logger.getGlobal().log(Level.SEVERE, "something's going on bud", exception.printStackTrace())
            null
        }
    }
}
