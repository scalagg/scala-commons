package net.evilblock.cubed.cache

import gg.scala.cache.uuid.ScalaStoreUuidCacheTranslator
import org.bukkit.Bukkit
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ConcurrentHashMap

object BukkitUuidCacheTranslator : ScalaStoreUuidCacheTranslator
{
    override val uniqueIdCache: ConcurrentHashMap<UUID, String>
        get() = TODO("Not yet implemented")
    override val usernameCache: ConcurrentHashMap<String, UUID>
        get() = TODO("Not yet implemented")

    override fun configure(): CompletionStage<Void>
    {
        return CompletableFuture.completedFuture(null)
    }

    override fun preLoadCache(async: Boolean)
    {

    }

    override fun update(pair: Pair<UUID, String>, commit: Boolean)
    {

    }

    override fun uniqueId(username: String): UUID?
    {
        return Bukkit.getOfflinePlayer(username)?.uniqueId
    }

    override fun username(uniqueId: UUID): String?
    {
        return Bukkit.getOfflinePlayer(uniqueId)?.name
    }

}
