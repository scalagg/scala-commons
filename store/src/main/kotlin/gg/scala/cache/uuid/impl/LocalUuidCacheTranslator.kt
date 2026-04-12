package gg.scala.cache.uuid.impl

import gg.scala.cache.uuid.ScalaStoreUuidCacheTranslator
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ConcurrentHashMap

object LocalUuidCacheTranslator : ScalaStoreUuidCacheTranslator
{
    override val uniqueIdCache = ConcurrentHashMap<UUID, String>()
    override val usernameCache = ConcurrentHashMap<String, UUID>()

    override fun configure(): CompletionStage<Void>
    {
        return CompletableFuture.completedFuture(null)
    }

    override fun preLoadCache(async: Boolean)
    {

    }

    override fun update(pair: Pair<UUID, String>, commit: Boolean)
    {
        usernameCache[pair.second.lowercase()] = pair.first
        uniqueIdCache[pair.first] = pair.second
    }

    override fun uniqueId(username: String): UUID?
    {
        return usernameCache[username.lowercase()]
    }

    override fun username(uniqueId: UUID): String?
    {
        return uniqueIdCache[uniqueId]
    }

}
