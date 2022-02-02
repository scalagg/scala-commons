package gg.scala.cache.uuid.impl.distribution

import com.google.gson.reflect.TypeToken
import gg.scala.banana.Banana
import gg.scala.banana.annotate.Subscribe
import gg.scala.banana.message.Message
import gg.scala.banana.subscribe.marker.BananaHandler
import gg.scala.cache.uuid.ScalaStoreUuidCacheTranslator
import net.evilblock.cubed.serializers.Serializers
import redis.clients.jedis.Jedis
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ForkJoinPool

/**
 * @author GrowlyX
 * @since 2/1/2022
 */
class DistributedRedisUuidCacheTranslator(
    private val banana: Banana
) : ScalaStoreUuidCacheTranslator
{
    companion object
    {
        const val USERNAME_REDIS_LOC = "DataStore:UuidCache:Username"
        const val UNIQUE_ID_REDIS_LOC = "DataStore:UuidCache:UUID"

        const val JEDIS_LISTENER_PACKET = "UuidCache-Update"

        @JvmStatic
        val KT_PAIR_TYPE: Type = object : TypeToken<Pair<UUID, String>>()
        {}.type
    }

    override val uniqueIdCache = ConcurrentHashMap<UUID, String>()
    override val usernameCache = ConcurrentHashMap<String, UUID>()

    /**
     * An initial startup method for this translator.
     */
    override fun configure()
    {
        banana.registerClass(object : BananaHandler
        {
            @Subscribe(JEDIS_LISTENER_PACKET)
            fun onUuidCacheUpdate(message: Message)
            {
                val pair = Serializers.gson
                    .fromJson<Pair<UUID, String>>(
                        message["data"]!!, KT_PAIR_TYPE
                    )

                update(pair, commit = false)
            }
        })
        banana.subscribe()
    }

    /**
     * Pre-loads the cache through whatever
     * service the implementation is using.
     */
    override fun preLoadCache(async: Boolean)
    {
        val loadFunction = {
            safeUse {
                val usernameEntries =
                    it.hgetAll(USERNAME_REDIS_LOC)

                for (entry in usernameEntries)
                {
                    usernameCache[entry.key] =
                        UUID.fromString(entry.value)
                }

                val uniqueIdEntries =
                    it.hgetAll(UNIQUE_ID_REDIS_LOC)

                for (entry in uniqueIdEntries)
                {
                    uniqueIdCache[UUID.fromString(entry.key)!!] = entry.value
                }
            }
        }

        if (async)
        {
            ForkJoinPool.commonPool()
                .execute {
                    loadFunction.invoke()
                }
        } else
        {
            loadFunction.invoke()
        }
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
        usernameCache[pair.second] = pair.first
        uniqueIdCache[pair.first] = pair.second.lowercase()

        if (commit)
        {
            ForkJoinPool.commonPool().execute {
                safeUse {
                    it.hset(USERNAME_REDIS_LOC, pair.second.lowercase(), pair.first.toString())
                    it.hset(UNIQUE_ID_REDIS_LOC, pair.first.toString(), pair.second.lowercase())
                }
            }

            Message(JEDIS_LISTENER_PACKET).apply {
                this["data"] = Serializers.gson.toJson(pair)
                this.dispatch(banana)
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
        return usernameCache[username] ?: internalUniqueId(username)
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
        return uniqueIdCache[uniqueId] ?: internalUsername(uniqueId)
    }

    private fun internalUsername(uniqueId: UUID): String?
    {
        return safeUse {
            it.hget(UNIQUE_ID_REDIS_LOC, uniqueId.toString())
        }
    }

    private fun internalUniqueId(username: String): UUID?
    {
        return safeUse {
            UUID.fromString(it.hget(USERNAME_REDIS_LOC, username)!!)
        }
    }

    private fun <T> safeUse(lambda: (Jedis) -> T): T?
    {
        return try
        {
            banana.useResource(lambda)
        } catch (exception: Exception)
        {
            exception.printStackTrace()
            null
        }
    }
}
