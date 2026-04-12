package gg.scala.commons.metadata

import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Limo
 * @date 2/5/2026
 */
object SpigotNetworkMetadataDataSync
{
    private val metadata = ConcurrentHashMap<UUID, NetworkMetadata>()
    private val flags = ConcurrentHashMap<String, Boolean>()

    data class NetworkMetadata(
        val uuid: UUID,
        val server: String? = null,
        val lastSeen: Long = System.currentTimeMillis(),
        val data: MutableMap<String, Any> = mutableMapOf()
    )

    fun get(player: Player): NetworkMetadata
    {
        return metadata.getOrPut(player.uniqueId) {
            NetworkMetadata(player.uniqueId)
        }
    }

    fun get(uuid: UUID): NetworkMetadata?
    {
        return metadata[uuid]
    }

    fun set(uuid: UUID, key: String, value: Any)
    {
        val meta = metadata.getOrPut(uuid) { NetworkMetadata(uuid) }
        meta.data[key] = value
    }

    fun remove(uuid: UUID)
    {
        metadata.remove(uuid)
    }

    fun getAll(): Map<UUID, NetworkMetadata> = metadata.toMap()

    fun sync(player: Player)
    {
        metadata[player.uniqueId] = NetworkMetadata(
            uuid = player.uniqueId,
            lastSeen = System.currentTimeMillis()
        )
    }

    fun unsync(player: Player)
    {
        metadata.remove(player.uniqueId)
    }

    fun isFlagged(flag: String): Boolean
    {
        return flags[flag] ?: false
    }

    fun setFlag(flag: String, value: Boolean)
    {
        flags[flag] = value
    }
}
