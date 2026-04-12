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

    }

    override fun preLoadCache(async: Boolean)
    {
        val loadFunction = {
            useAndReturn {
                val usernameEntries =
                    sync().hgetall(USERNAME_REDIS_LOC)

                for (entry in usernameEntries)
                {
                    usernameCache[entry.key] =
                        UUID.fromString(entry.value)
                }

                val uniqueIdEntries =
                    sync().hgetall(UNIQUE_ID_REDIS_LOC)

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

    override fun update(
        pair: Pair<UUID, String>, commit: Boolean
    )
    {
        val updateRequired =
            usernameCache[pair.second.lowercase()] == null ||
                    uniqueIdCache[pair.first] == null

        usernameCache[pair.second.lowercase()] = pair.first
        uniqueIdCache[pair.first] = pair.second

        if (commit && updateRequired)
        {
            CompletableFuture
                .runAsync {
                    useAndReturn {
                        this.sync().apply {
                            hset(USERNAME_REDIS_LOC, pair.second.lowercase(), pair.first.toString())
                            hset(UNIQUE_ID_REDIS_LOC, pair.first.toString(), pair.second)
                        }
                    }
                }
        }
    }

    override fun uniqueId(username: String): UUID?
    {
        return kotlin.runCatching {
            usernameCache[username.lowercase()]
                ?: internalUniqueId(username)
        }.getOrNull()
    }

    override fun username(uniqueId: UUID): String?
    {
        return uniqueIdCache[uniqueId] ?: internalUsername(uniqueId)
    }

    private fun internalUsername(uniqueId: UUID): String?
    {
        return useAndReturn {
            sync().hget(UNIQUE_ID_REDIS_LOC, uniqueId.toString())
        }
    }

    private fun internalUniqueId(username: String): UUID?
    {
        return useAndReturn {
            val nullable = this.sync()
                .hget(USERNAME_REDIS_LOC, username.lowercase())
                ?: return@useAndReturn null

            UUID.fromString(nullable)
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
            exception.printStackTrace()
            null
        }
    }
}
