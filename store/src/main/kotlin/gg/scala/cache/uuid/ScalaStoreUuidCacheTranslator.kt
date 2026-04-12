package gg.scala.cache.uuid

import java.io.Closeable
import java.util.*
import java.util.concurrent.CompletionStage

interface ScalaStoreUuidCacheTranslator : Closeable
{
    val uniqueIdCache: MutableMap<UUID, String>
    val usernameCache: MutableMap<String, UUID>

    override fun close()
    {

    }

    fun configure(): CompletionStage<Void>

    fun preLoadCache(async: Boolean = true)

    fun update(pair: Pair<UUID, String>, commit: Boolean = true)

    fun uniqueId(username: String): UUID?

    fun username(uniqueId: UUID): String?
}
