package gg.scala.commons.agnostic.sync.server

import gg.scala.commons.agnostic.sync.server.metadata.MetadataValue
import gg.scala.commons.agnostic.sync.server.region.Region
import gg.scala.commons.agnostic.sync.server.state.ServerState
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable
import net.kyori.adventure.key.Key
import net.kyori.adventure.key.KeyedValue
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author GrowlyX
 * @since 8/29/2022
 */
abstract class AbstractServer : AbstractTypeSerializable
{
    abstract val id: String
    abstract val groups: List<String>
    abstract val region: Region

    var state = ServerState.Connected

    val metadata = CopyOnWriteArrayList<Pair<Key, MetadataValue>>()

    var lastHeartbeat = System.currentTimeMillis()
    var initialHeartbeat = System.currentTimeMillis()

    inline fun <reified T> getMetadataValue(namespace: String, key: String) =
        this.getMetadata(namespace, key)?.cast<T>()

    fun getMetadata(namespace: String, key: String) =
        this.metadata
            .find {
                it.first.asString() == "$namespace:$key"
            }
            ?.second

    fun setMetadata(namespace: String, key: String, value: Any) =
        this.setMetadata(Key.key(namespace, key), value)

    fun setMetadata(key: Key, value: Any) =
        this.setMetadata(key, MetadataValue(value))

    fun setMetadata(key: Key, value: MetadataValue)
    {
        this.metadata.removeIf {
            it.first.asString() == key.asString()
        }

        this.metadata.add(key to value)
    }
}
